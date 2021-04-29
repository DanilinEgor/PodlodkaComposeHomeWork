package com.danegor.podlodkahw

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.danegor.podlodkahw.data.SessionsListApi
import com.danegor.podlodkahw.data.SessionsRepository
import com.danegor.podlodkahw.ui.info.SessionInfoScreen
import com.danegor.podlodkahw.ui.list.SessionsListScreen
import com.danegor.podlodkahw.ui.theme.PodlodkaHWTheme
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MainActivity : ComponentActivity() {
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appContainer = (application as PodlodkaHWApp).container

        setContent {
            PodlodkaHWTheme {
                Surface(color = MaterialTheme.colors.background) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = Route.List.getPattern()
                    ) {
                        composable(Route.List.getPattern()) {
                            SessionsListScreen(
                                navController = navController,
                                sessionsRepository = appContainer.sessionsRepository,
                                onExit = { finish() }
                            )
                        }

                        composable(Route.Info.getPattern()) {
                            SessionInfoScreen(
                                sessionId = it.arguments?.getString("sessionId"),
                                navController = navController,
                                sessionsRepository = appContainer.sessionsRepository
                            )
                        }
                    }
                }
            }
        }
    }
}