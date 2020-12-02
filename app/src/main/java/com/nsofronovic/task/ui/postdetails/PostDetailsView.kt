package com.nsofronovic.task.ui.postdetails

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable

interface PostDetailsView : MvpView {
    fun render(state: PostDetailsViewState)
    fun initialIntent(): Observable<Unit>
    fun deletePostIntent(): Observable<Unit>
}