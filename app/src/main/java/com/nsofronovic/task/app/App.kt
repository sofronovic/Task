package com.nsofronovic.task.app

import android.app.Application
import com.nsofronovic.task.BuildConfig
import com.nsofronovic.task.module.appModule
import com.nsofronovic.task.module.dbModule
import com.nsofronovic.task.module.mviModule
import com.nsofronovic.task.module.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidContext(this@App)

            modules(
                listOf(
                    appModule,
                    networkModule(BuildConfig.SERVER_URL),
                    mviModule, dbModule(this@App, BuildConfig.POST_APP_DATABASE_NAME)
                )
            )
        }
    }
}
