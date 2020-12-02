package com.nsofronovic.task.service

import android.content.Intent
import com.nsofronovic.task.ui.ContainerActivity
import timber.log.Timber

class ServiceManager {

    lateinit var activity: ContainerActivity

    fun startService() {
        if (!DatabaseService.isServiceRunning) {
            val intent = Intent(activity, DatabaseService::class.java)
            DatabaseService.enqueueWork(
                activity, intent
            )
        }
    }
}