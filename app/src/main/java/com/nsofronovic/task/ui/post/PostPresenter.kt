package com.nsofronovic.task.ui.post

import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.Observable

/**
 * PostPresenter is responsible for merging View-Intents with business logic.
 *
 **/
class PostPresenter(private val interactor: PostInteractor) :
    MviBasePresenter<PostView, PostViewState>() {

    lateinit var currentState: PostViewState

    /**
     * bindIntent() called only once at after view is attached to Presenter.
     * Intents from PostView are bonded here, and method survives orientation changes
     *
     **/
    override fun bindIntents() {
        currentState = PostViewState()

        val initialIntent = intent(PostView::initialIntent)
            .switchMap {
                if (currentState.posts.isNullOrEmpty()) {
                    interactor.generateInitialPartialState()
                } else {
                    currentState.posts?.let { posts ->
                        interactor.generateLoadPostsFromStatePartialState(posts)
                    }
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

        val startServiceIntent = intent(PostView::startServiceIntent)
            .switchMap {
                interactor.generateStartServiceIntent()
            }

        val intentsObservable =
            Observable.mergeArray(
                initialIntent,
                swipeToRefreshIntent,
                onPostClickIntent,
                onPauseIntent,
                startServiceIntent
            )

        subscribeViewState(
            intentsObservable.scan<PostViewState>(
                currentState, this::stateReducer
            ).distinctUntilChanged(), PostView::render
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
                lastChangedState = partialState,
                posts = partialState.posts
            )
            else -> previousState.copy(lastChangedState = partialState)
        }

        currentState = newState

        return currentState
    }
}
