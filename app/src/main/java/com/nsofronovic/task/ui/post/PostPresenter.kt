package com.nsofronovic.task.ui.post

import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.Observable

class PostPresenter(val interactor: PostInteractor) :
    MviBasePresenter<PostView, PostViewState>() {

    private lateinit var currentState: PostViewState

    override fun bindIntents() {
        currentState = PostViewState()

        val initialIntent = intent(PostView::initialIntent)
            .switchMap {
                if (currentState.posts.isNullOrEmpty()) {
                    interactor.generateInitialPartialState()
                } else {
                    interactor.generateLoadPostsFromStatePartialState()
                }
            }

        val swipeToRefreshIntent = intent(PostView::swipeToRefreshIntent)
            .switchMap {
                interactor.generateSwipeToRefreshPartialState()
            }

        val onPostClickIntent = intent(PostView::onPostClickIntent)
            .switchMap { postPosition ->
                interactor.generatePostClickPartialState(currentState.posts?.get(postPosition))
            }

        val onPauseIntent = intent(PostView::onPauseIntent)
            .switchMap {
                interactor.generateOnPausePartialState()
            }

        val intentsObservable =
            Observable.mergeArray(
                initialIntent, swipeToRefreshIntent, onPostClickIntent, onPauseIntent
            )

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
            is PostPartialState.LoadedPosts -> previousState.copy(
                lastChangedState = partialState,
                posts = partialState.posts
            )
            is PostPartialState.LoadedPostsFromDatabase -> previousState.copy(
                lastChangedState = partialState,
                posts = partialState.posts
            )
            is PostPartialState.PostClicked -> previousState.copy(
                lastChangedState = partialState
            )
            is PostPartialState.LoadedPostsFromState -> previousState.copy(
                lastChangedState = partialState
            )
            else -> previousState.copy(lastChangedState = partialState)
        }

        currentState = newState

        return currentState
    }
}
