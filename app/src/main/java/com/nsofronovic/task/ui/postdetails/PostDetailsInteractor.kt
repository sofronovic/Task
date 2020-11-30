package com.nsofronovic.task.ui.postdetails

import com.nsofronovic.task.model.PostDetailsData
import com.nsofronovic.task.model.User
import com.nsofronovic.task.repository.local.PostLocalRepository
import com.nsofronovic.task.repository.local.UserLocalRepository
import com.nsofronovic.task.repository.remote.UserRepository
import com.nsofronovic.task.util.NetworkUtil
import io.reactivex.Maybe
import io.reactivex.Observable

class PostDetailsInteractor(
    private val userRepository: UserRepository,
    private val localPostRepository: PostLocalRepository,
    private val userLocalRepository: UserLocalRepository,
    private val networkUtil: NetworkUtil
) {

    fun generateInitialPartialState(): Observable<PostDetailsPartialState> {
        val currentPost = localPostRepository.getCurrentPost()
        return networkUtil.isConnectedToInternet()
            .flatMapObservable { isConnected ->
                if (isConnected) {
                    userRepository.getUser(
                        currentPost.userId
                    ).flatMap { user ->
                        Observable.merge(
                            Observable.just(PostDetailsPartialState.LoadingUser),
                            saveUserToDatabase(user),
                            Observable.just(
                                PostDetailsPartialState.LoadedPostDetails(
                                    PostDetailsData(
                                        id = currentPost.userId,
                                        title = currentPost.title,
                                        body = currentPost.body,
                                        name = user.name,
                                        email = user.email
                                    )
                                )
                            )
                        )
                    }.onErrorReturn { throwable ->
                        throwable.message?.let { errorMessage ->
                            PostDetailsPartialState.ErrorLoadUser(errorMessage)
                        }
                    }
                } else {
                    Observable.merge(
                        Observable.just(PostDetailsPartialState.LoadingUser),
                        getUserFromDatabase(currentPost.userId)
                            .flatMapObservable { user ->
                                Observable.just(
                                    PostDetailsPartialState.LoadedPostDetails(
                                        PostDetailsData(
                                            id = currentPost.userId,
                                            title = currentPost.title,
                                            body = currentPost.body,
                                            name = user.name,
                                            email = user.email
                                        )
                                    )
                                )
                            }
                    )
                }
            }
    }

    private fun saveUserToDatabase(user: User): Observable<PostDetailsPartialState> {
        return getUserFromDatabase(user.id)
            .isEmpty
            .flatMapObservable { isRowEmpty ->
                if (isRowEmpty) {
                    userLocalRepository.insert(user).flatMapObservable { userId ->
                        Observable.just(PostDetailsPartialState.SavedUserToDatabase(userId))
                    }
                } else {
                    Observable.just(PostDetailsPartialState.UserAlreadyExistsInDatabase)
                }
            }.onErrorReturn { throwable ->
                throwable.message?.let { errorMessage ->
                    PostDetailsPartialState.ErrorSavingUserInDatabase(errorMessage)
                }
            }
    }

    private fun getUserFromDatabase(userId: Int): Maybe<User> {
        return userLocalRepository.getUserById(userId)
    }

}
