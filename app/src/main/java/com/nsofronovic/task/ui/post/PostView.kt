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

    /**
     * swipeToRefreshIntent() event is fired when swipe to refresh is triggered
     *
     **/
    fun swipeToRefreshIntent(): Observable<Unit>

    /**
     * onPostClickIntent() event is fired when single post item is clicked
     *
     **/
    fun onPostClickIntent(): Observable<Int>

    /**
     * onPauseIntent() event is fired when app goes to the background
     *
     **/
    fun onPauseIntent(): Observable<Unit>

    /**
     * startServiceIntent() event is fired when database service is called
     *
     **/
    fun startServiceIntent(): Observable<Unit>
}
