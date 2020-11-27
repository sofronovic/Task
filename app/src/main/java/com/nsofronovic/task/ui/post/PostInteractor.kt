package com.nsofronovic.task.ui.post

import io.reactivex.Observable

class PostInteractor {
    fun generateInitialPartialState(): Observable<PostPartialState> {
        return Observable.just(PostPartialState.InitialPartialState)
    }
}