package com.nsofronovic.task.service

import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
import com.nsofronovic.task.repository.local.PostLocalRepository
import org.koin.android.ext.android.inject
import timber.log.Timber
import kotlin.random.Random

class DatabaseService : JobIntentService() {

    private val postLocalRepository: PostLocalRepository by inject()

    private val dataValidityTime = 300000L

    companion object {
        var isServiceRunning = false
        private const val JOB_ID = 1000

        fun enqueueWork(context: Context, work: Intent) {
            enqueueWork(context, DatabaseService::class.java, JOB_ID, work)
        }
    }

    override fun onHandleWork(intent: Intent) {
        Timber.d("Starting background service")
        try {
            isServiceRunning = true
            Timber.d("Thread sleep")
            Thread.sleep(dataValidityTime)
            Timber.d("Thread is awake")
            Timber.d("Deleting posts...")
            postLocalRepository.deleteAll().subscribe()
        } catch (ex: Exception) {
            Timber.e(ex)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isServiceRunning = false
        Timber.d("Service job is done!")
    }
}
