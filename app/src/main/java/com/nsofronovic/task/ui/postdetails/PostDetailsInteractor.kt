package com.nsofronovic.task.ui.postdetails

import com.nsofronovic.task.model.PostDetailsData
import com.nsofronovic.task.model.User
import com.nsofronovic.task.repository.local.PostLocalRepository
import com.nsofronovic.task.repository.local.UserLocalRepository
import com.nsofronovic.task.repository.remote.UserRepository
import com.nsofronovic.task.util.NetworkUtil
import io.reactivex.Maybe
import io.reactivex.Observable

/**
 * PostDetailsInteractor is defined as placeholder for business logic.
 * PostDetailsInteractor offers the Observables stream Observable<PostPartialState> and
 * broadcast a new PostPartialState each time the state changes.
 *
 **/
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
                                        id = currentPost.id,
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
                    getUserFromDatabase(currentPost.userId)
                        .toSingle()
                        .flatMapObservable { user ->
                            Observable.merge(
                                Observable.just(PostDetailsPartialState.LoadingUser),
                                Observable.just(
                                    PostDetailsPartialState.LoadedPostDetails(
                                        PostDetailsData(
                                            id = currentPost.id,
                                            title = currentPost.title,
                                            body = currentPost.body,
                                            name = user.name,
                                            email = user.email
                                        )
                                    )
                                )
                            )
                        }.onErrorReturn {
                            PostDetailsPartialState.NoInternetConnection
                        }
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

    fun generateDeletePostPartialState(): Observable<PostDetailsPartialState> {
        val currentPost = localPostRepository.getCurrentPost()
        return localPostRepository.delete(currentPost).flatMapObservable { deletedRows ->
            if (deletedRows > 0) {
                localPostRepository.setDeletedPost(currentPost)
                Observable.just(PostDetailsPartialState.DeletedPostFromDatabase)
            } else {
                Observable.just(PostDetailsPartialState.FailedToDeletePost)
            }
        }
    }

}
