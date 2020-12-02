package com.nsofronovic.task.service

import android.content.Intent
import com.nsofronovic.task.ui.ContainerActivity

class ServiceManager {

    lateinit var activity: ContainerActivity

    fun startService() {
        val intent = Intent(activity, DatabaseService::class.java)
        DatabaseService.enqueueWork(
            activity, intent
        )
    }
}