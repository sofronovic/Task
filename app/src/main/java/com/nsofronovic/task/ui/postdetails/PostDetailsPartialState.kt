package com.nsofronovic.task.ui.postdetails

import com.nsofronovic.task.model.PostDetailsData

/**
 * PostDetailsPartialState holds objects and data classes to represent state
 *
 **/
sealed class PostDetailsPartialState {
    object LoadingUser : PostDetailsPartialState()
    object UserAlreadyExistsInDatabase : PostDetailsPartialState()
    data class LoadedPostDetails(val postDetailsData: PostDetailsData) : PostDetailsPartialState()

    data  class ErrorLoadUser(val errorMessage: String) : PostDetailsPartialState()
    data class SavedUserToDatabase(val userId: Long) : PostDetailsPartialState()
    data class ErrorSavingUserInDatabase(val errorMessage: String) : PostDetailsPartialState()

    object DeletedPostFromDatabase : PostDetailsPartialState()
    object FailedToDeletePost : PostDetailsPartialState()

    object NoInternetConnection : PostDetailsPartialState()

}