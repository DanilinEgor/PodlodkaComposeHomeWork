package com.danegor.podlodkahw.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.danegor.podlodkahw.Session
import com.danegor.podlodkahw.data.SessionsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SessionsListViewModel(
    private val repository: SessionsRepository
) : ViewModel() {
    private val baseList = mutableListOf<Session>()
    private val baseUiList = mutableListOf<SessionUiModel>()
    private val sessionsListFlow = MutableSharedFlow<List<Session>>()

    private val _stateFlow = MutableStateFlow(SessionsListScreenState())
    val stateFlow: StateFlow<SessionsListScreenState> = _stateFlow

    init {
        refresh()

        viewModelScope.launch {
            combine(
                sessionsListFlow,
                repository.getFavourites()
            ) { sessions: List<Session>, favourites: List<String> ->
                val result = mutableListOf<SessionUiModel>()
                if (favourites.isNotEmpty()) {
                    result.add(SessionFavouriteTitleUiModel)
                    result.add(SessionFavouritesUiModel(baseList.filter { favourites.contains(it.id) }))
                }

                if (sessions.isNotEmpty()) result.add(SessionLinksTitleUiModel)

                val dateSessionMap = sessions.groupBy { it.date }
                val dates = dateSessionMap.keys.sorted()
                dates.forEach { date ->
                    result.add(SessionDateTitleUiModel(date))
                    dateSessionMap[date]?.forEach {
                        result.add(SessionCardUiModel(it, favourites.contains(it.id)))
                    }
                }

                result
            }.collect {
                _stateFlow.value = SessionsListScreenState(list = it)
            }
        }
    }

    fun refresh() {
        _stateFlow.value = _stateFlow.value.copy(isLoading = true)
        viewModelScope.launch {
            repository.getSessionsList()
                .flowOn(Dispatchers.IO)
                .collect {
                    if (it.isSuccess) {
                        baseList.clear()
                        it.getOrNull()?.let { baseList.addAll(it) }
                        sessionsListFlow.emit(baseList.toList())
                    } else {
                        _stateFlow.value = SessionsListScreenState(isError = true)
                    }
                }
        }
    }

    fun setSessionFavourite(sessionId: String, isFavourite: Boolean): Boolean {
        return repository.setSessionFavourite(sessionId, isFavourite)
    }

    fun onSearchText(input: String) {
        if (input.isBlank()) {
            sessionsListFlow.tryEmit(baseList.toList())
            return
        }

        sessionsListFlow.tryEmit(baseList.filter {
            it.description.contains(input, ignoreCase = true) ||
                it.speaker.contains(input, ignoreCase = true)
        })
    }
}

class SessionsListViewModelFactory(
    private val sessionsRepository: SessionsRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SessionsListViewModel(sessionsRepository) as T
    }
}

data class SessionsListScreenState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val list: List<SessionUiModel>? = null
)