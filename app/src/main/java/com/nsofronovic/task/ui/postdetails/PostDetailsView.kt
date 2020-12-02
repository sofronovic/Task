package com.nsofronovic.task.ui.postdetails

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable

/*
 * PostDetailsView represents interface where intents are defined as user actions
 *
 **/
interface PostDetailsView : MvpView {
    /**
     * Renders the View
     *
     * @param state the current viewState that should be displayed
     */
    fun render(state: PostDetailsViewState)

    /**
     * initialIntent() event is fired at PostFragment resume state
     *
     **/
    fun initialIntent(): Observable<Unit>

    /**
     * initialIntent() event is fired when delete user is triggered
     *
     **/
    fun deletePostIntent(): Observable<Unit>
}