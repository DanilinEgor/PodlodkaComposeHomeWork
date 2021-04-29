package com.danegor.podlodkahw

import android.app.Application
import com.danegor.podlodkahw.data.SessionsListApi
import com.danegor.podlodkahw.data.SessionsRepository
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class PodlodkaHWApp : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainerImpl()
    }
}

interface AppContainer {
    val sessionsRepository: SessionsRepository
}

class AppContainerImpl() : AppContainer {
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://gist.githubusercontent.com/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    private val api: SessionsListApi by lazy {
        retrofit.create(SessionsListApi::class.java)
    }

    override val sessionsRepository: SessionsRepository by lazy {
        SessionsRepository(api)
    }
}