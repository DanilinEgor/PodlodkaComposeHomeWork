package com.danegor.podlodkahw

sealed class Route(val route: String) {
    object List : Route("list") {
        fun getPattern(): String = route
    }

    data class Info(val sessionId: String) : Route("info/$sessionId") {
        companion object {
            fun getPattern(): String = "info/{sessionId}"
        }
    }
}