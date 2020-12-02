package com.nsofronovic.task.ui.post

import com.nsofronovic.task.model.Post
/**
 * PostPartialState holds objects and data classes to represent state
 *
 **/
sealed class PostPartialState {
    object LoadingPosts : PostPartialState()
    data class LoadedPosts(val posts: List<Post>) : PostPartialState()
    data class ErrorLoadingPosts(val error: String) : PostPartialState()

    data class LoadedPostsFromDatabase(val posts: List<Post>) : PostPartialState()
    object PostsSavedToDatabase : PostPartialState()
    data class ErrorSavingPostsInDatabase(val error: String) : PostPartialState()
    object NoPostsAddedToDatabase : PostPartialState()

    data class LoadedPostsFromState(val posts: List<Post>) : PostPartialState()

    object SwipeToRefresh : PostPartialState()

    object OnPause : PostPartialState()

    object PostClicked : PostPartialState()
    object NoInternetConnection : PostPartialState()
    object StartDatabaseService : PostPartialState()
}
