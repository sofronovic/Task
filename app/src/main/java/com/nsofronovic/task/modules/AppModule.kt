package com.nsofronovic.task.modules

import com.nsofronovic.task.ui.navigation.NavigationManager
import com.nsofronovic.task.ui.post.PostInteractor
import com.nsofronovic.task.ui.post.PostPresenter
import org.koin.dsl.module

val mviModule = module {
    factory { PostPresenter(get()) }
    factory { PostInteractor() }
}

val appModule = module {
    single { NavigationManager() }
}