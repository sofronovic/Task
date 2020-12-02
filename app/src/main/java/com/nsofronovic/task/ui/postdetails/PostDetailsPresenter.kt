package com.nsofronovic.task.ui.postdetails

import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.Observable

/**
 * PostDetailsPresenter is responsible for merging View-Intents with business logic.
 *
 **/
class PostDetailsPresenter(private val interactor: PostDetailsInteractor) :
    MviBasePresenter<PostDetailsView, PostDetailsViewState>() {

    lateinit var currentState: PostDetailsViewState

    /**
     * bindIntent() called only once at after view is attached to Presenter.
     * Intents from PostView are bonded here, and method survives orientation changes
     *
     **/
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

    /**
     * stateReducer() takes the previous state as input and
     * creates a new state from the previous state
     *
     * @param previousState represents previous state of the app
     * @param partialState represents new state of the app
     */
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