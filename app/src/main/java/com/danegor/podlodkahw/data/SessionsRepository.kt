package com.danegor.podlodkahw.data

import com.danegor.podlodkahw.Session
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import retrofit2.http.GET

class SessionsRepository(private val api: SessionsListApi) {
    private val sessionsList = mutableListOf<Session>()
    private val favouritesList = mutableListOf<String>()
    private val favouritesStateFlow = MutableStateFlow<List<String>>(emptyList())

    fun getFavourites(): StateFlow<List<String>> = favouritesStateFlow.asStateFlow()

    fun getSessionsList(): Flow<Result<List<Session>>> = flow {
        delay(1000)
        val remoteList = loadRemoteList()
        sessionsList.clear()
        sessionsList.addAll(remoteList)
        emit(Result.success(sessionsList))
    }.catch {
        emit(Result.failure(it))
    }

    fun setSessionFavourite(sessionId: String, isFavourite: Boolean): Boolean {
        if (isFavourite && favouritesList.size >= 3) return false

        if (isFavourite) {
            favouritesList.add(sessionId)
        } else {
            favouritesList.remove(sessionId)
        }
        favouritesStateFlow.value = favouritesList.toList()
        return true
    }

    fun getSession(sessionId: String): Session {
        return sessionsList.first { it.id == sessionId }
    }

    private suspend fun loadRemoteList(): List<Session> = api.getSessionsList()
}

interface SessionsListApi {
    @GET("/AJIEKCX/901e7ae9593e4afd136abe10ca7d510f/raw/61e7c1f037345370cf28b5ae6fdaffdd9e7e18d5/Sessions.json")
    suspend fun getSessionsList(): List<Session>
}

data class Result2<T>(
    val isLoading: Boolean = false,
    val value: T? = null,
    val error: Throwable? = null
)