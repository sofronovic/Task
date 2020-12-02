package com.nsofronovic.task.ui.postdetails

import com.nsofronovic.task.model.PostDetailsData

/**
 * PostViewState represents model as a state
 *
 * @param lastChangedState
 * @param postDetailsData post and user data that should be displayed on UI
 */
data class PostDetailsViewState(
    val lastChangedState: PostDetailsPartialState? = null,
    val postDetailsData: PostDetailsData? = null
)