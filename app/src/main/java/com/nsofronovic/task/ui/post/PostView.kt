package com.nsofronovic.task.ui.post

import android.content.Intent
import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable

interface PostView : MvpView {
    fun render(state: PostViewState)
    fun initialIntent(): Observable<Unit>
    fun swipeToRefreshIntent(): Observable<Unit>
    fun onPostClickIntent(): Observable<Int>
    fun onPauseIntent(): Observable<Unit>
    fun startServiceIntent(): Observable<Unit>
}
