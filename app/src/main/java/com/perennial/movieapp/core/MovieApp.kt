package com.perennial.movieapp.core

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MovieApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MovieApp)
            modules(module)
            modules(uiModule)
        }
    }
}