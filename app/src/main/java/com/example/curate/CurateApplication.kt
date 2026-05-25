package com.example.curate

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class CurateApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.LOGS_ENABLED) {
            Timber.plant(Timber.DebugTree())
            Timber.d("Curate application started")
        }
    }
}