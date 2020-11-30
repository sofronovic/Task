package com.nsofronovic.task.ui.navigation

import androidx.navigation.findNavController
import com.nsofronovic.task.R
import com.nsofronovic.task.ui.ContainerActivity
import com.nsofronovic.task.ui.post.PostFragmentDirections

class NavigationManager {

    lateinit var activity: ContainerActivity

    fun openPostDetailsScreen() {
        activity.findNavController(R.id.navHostFragment)
            .navigate(PostFragmentDirections.actionFragmentPostToFragmentPostDetails())
    }
}
