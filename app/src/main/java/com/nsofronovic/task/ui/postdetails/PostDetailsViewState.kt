package com.nsofronovic.task.ui.postdetails

import com.nsofronovic.task.model.PostDetailsData

data class PostDetailsViewState(
    val lastChangedState: PostDetailsPartialState? = null,
    val postDetailsData: PostDetailsData? = null
)