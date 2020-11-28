package com.nsofronovic.task.ui.post

import com.nsofronovic.task.model.Post

sealed class PostPartialState {
    data class LoadedPostsPartialState(val posts: List<Post>) : PostPartialState()
    object LoadingPostsPartialState : PostPartialState()
    object ErrorLoadingPostsPartialState : PostPartialState()
}
