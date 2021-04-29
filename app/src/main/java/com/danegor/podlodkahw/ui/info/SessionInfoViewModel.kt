package com.danegor.podlodkahw.ui.info

import androidx.lifecycle.ViewModel
import com.danegor.podlodkahw.MockSessions
import com.danegor.podlodkahw.Session

class SessionInfoViewModel : ViewModel() {
    fun dataFlow(sessionId: String): Session {
        return MockSessions.first { it.id == sessionId }
    }
}