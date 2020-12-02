package com.nsofronovic.task.ui.postdetails

import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.Observable

class PostDetailsPresenter(private val interactor: PostDetailsInteractor) :
    MviBasePresenter<PostDetailsView, PostDetailsViewState>() {

    lateinit var currentState: PostDetailsViewState

    override fun bindIntents() {
        currentState = PostDetailsViewState()

        val initialIntent = intent(PostDetailsView::initialIntent)
            .switchMap {
                interactor.generateInitialPartialState()
            }

        val deletePost = intent(PostDetailsView::deletePostIntent)
            .switchMap {
                interactor.generateDeletePostPartialState()
            }

        val intentsObservable =
            Observable.mergeArray(initialIntent, deletePost)

        subscribeViewState(
            intentsObservable.scan<PostDetailsViewState>(
                currentState, this::stateReducer
            ).distinctUntilChanged(), PostDetailsView::render
        )
    }

    private fun stateReducer(
        previousState: PostDetailsViewState,
        partialState: PostDetailsPartialState
    ): PostDetailsViewState {
        val newState = when (partialState) {
            is PostDetailsPartialState.LoadedPostDetails -> previousState.copy(
                lastChangedState = partialState,
                postDetailsData = partialState.postDetailsData
            )
            else -> previousState.copy(lastChangedState = partialState)
        }

        currentState = newState

        return currentState
    }

}