package com.danegor.podlodkahw.ui.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.danegor.podlodkahw.Session
import com.danegor.podlodkahw.data.SessionsRepository

class SessionInfoViewModel(
    private val sessionsRepository: SessionsRepository
) : ViewModel() {
    fun getSession(sessionId: String): Session = sessionsRepository.getSession(sessionId)
}

class SessionInfoViewModelFactory(
    private val sessionsRepository: SessionsRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SessionInfoViewModel(sessionsRepository) as T
    }
}