package com.nsofronovic.task.ui.post

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable

/*
 * PostView represents interface where intents are defined as user actions
 *
 **/
interface PostView : MvpView {
    /**
     * Renders the View
     *
     * @param state the current viewState that should be displayed
     */
    fun render(state: PostViewState)

    /**
     * initialIntent() event is fired at PostFragment resume state
     *
     **/
    fun initialIntent(): Observable<Unit>
    fun swipeToRefreshIntent(): Observable<Unit>
    fun onPostClickIntent(): Observable<Int>
    fun onPauseIntent(): Observable<Unit>
    fun startServiceIntent(): Observable<Unit>
}
