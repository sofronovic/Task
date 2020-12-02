package com.nsofronovic.task.ui.post

import com.nsofronovic.task.model.Post
/**
 * PostViewState represents model as a state
 *
 * @param lastChangedState
 * @param posts list of post that should be displayed on UI
 */
data class PostViewState(
    val lastChangedState: PostPartialState? = null,
    val posts: List<Post>? = null
)
