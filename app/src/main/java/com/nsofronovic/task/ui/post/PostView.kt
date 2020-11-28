package com.nsofronovic.task.ui.post

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable

interface PostView : MvpView {
    fun render(state: PostViewState)
    fun initialIntent(): Observable<Unit>
}
