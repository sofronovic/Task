package com.nsofronovic.task.app

import android.app.Application
import com.nsofronovic.task.modules.appModule
import com.nsofronovic.task.modules.mviModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)

            modules(listOf(mviModule, appModule))
        }
    }
}