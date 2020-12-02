package com.nsofronovic.task.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.nsofronovic.task.R
import com.nsofronovic.task.service.ServiceManager
import com.nsofronovic.task.ui.navigation.NavigationManager
import org.koin.android.ext.android.inject

/**
 * ContainerActivity is defined as container for fragments.
 * Holds a reference to NavigationController that is used for navigation
 **/
class ContainerActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment

    private val navigationManager: NavigationManager by inject()
    private val serviceManager: ServiceManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController

        navigationManager.activity = this
        serviceManager.activity = this
    }
}
