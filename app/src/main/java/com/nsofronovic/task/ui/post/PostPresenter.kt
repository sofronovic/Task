package com.nsofronovic.task.ui.post

import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers

class PostPresenter(val interactor: PostInteractor) :
    MviBasePresenter<PostView, PostViewState>() {

    private lateinit var currentState: PostViewState

    override fun bindIntents() {
        currentState = PostViewState()

        val initialIntent = intent(PostView::initialIntent)
            .switchMap {
                interactor.generateInitialPartialState()
            }

        val intentsObservable =
            Observable.mergeArray(
                initialIntent
            ).observeOn(AndroidSchedulers.mainThread())

        subscribeViewState(
            intentsObservable.scan<PostViewState>(
                currentState, this::stateReducer
            ).distinctUntilChanged(), PostView::render
        )
    }

    private fun stateReducer(
        previousState: PostViewState,
        partialState: PostPartialState
    ): PostViewState {
        val newState = when (partialState) {
            is PostPartialState.LoadedPostsPartialState -> previousState.copy(
                lastChangedState = partialState,
                posts = partialState.posts
            )
            else -> previousState.copy(lastChangedState = partialState)
        }

        currentState = newState

        return currentState
    }
}
