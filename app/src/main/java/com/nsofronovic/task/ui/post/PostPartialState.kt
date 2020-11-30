package com.nsofronovic.task.ui.post

import com.nsofronovic.task.model.Post

sealed class PostPartialState {
    object LoadingPosts : PostPartialState()
    data class LoadedPosts(val posts: List<Post>) : PostPartialState()
    data class ErrorLoadingPosts(val error: String) : PostPartialState()

    data class LoadedPostsFromDatabase(val posts: List<Post>) : PostPartialState()
    object PostsSavedToDatabase : PostPartialState()
    data class ErrorSavingPostsInDatabase(val error: String) : PostPartialState()
    object PostsAlreadyExistsInDatabase : PostPartialState()

    object SwipeToRefresh : PostPartialState()

    object OnPause : PostPartialState()

    object LoadedPostsFromState : PostPartialState()

    object PostClicked : PostPartialState()
}
