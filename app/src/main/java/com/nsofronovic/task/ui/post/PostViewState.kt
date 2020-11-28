package com.nsofronovic.task.ui.post

import com.nsofronovic.task.model.Post

data class PostViewState(
    val lastChangedState: PostPartialState? = null,
    val posts: List<Post>? = null
)
