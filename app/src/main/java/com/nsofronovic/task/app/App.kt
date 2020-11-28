package com.nsofronovic.task.app

import android.app.Application
import com.nsofronovic.task.BuildConfig
import com.nsofronovic.task.module.appModule
import com.nsofronovic.task.module.mviModule
import com.nsofronovic.task.module.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)

            modules(listOf(appModule,
                networkModule(BuildConfig.SERVER_URL_DEBUG),
                mviModule))
        }
    }
}
