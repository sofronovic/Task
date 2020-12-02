package com.nsofronovic.task.ui.post

import com.nsofronovic.task.model.Post
import com.nsofronovic.task.repository.local.PostLocalRepository
import com.nsofronovic.task.repository.remote.PostRepository
import com.nsofronovic.task.util.NetworkUtil
import io.reactivex.Observable
import io.reactivex.Single

class PostInteractor(
    private val postRepository: PostRepository,
    private val postLocalRepository: PostLocalRepository,
    private val networkUtil: NetworkUtil
) {
    fun generateInitialPartialState(): Observable<PostPartialState> {
        return networkUtil.isConnectedToInternet()
            .flatMapObservable { isConnected ->
                if (isConnected) {
                    postRepository.getPosts()
                        .flatMap { posts ->
                            Observable.merge(
                                Observable.just(PostPartialState.LoadingPosts),
                                savePostsToDatabasePartialState(posts),
                                Observable.just(
                                    PostPartialState.LoadedPosts(posts)
                                )
                            )
                        }.onErrorReturn { throwable ->
                            throwable.message?.let { errorMessage ->
                                PostPartialState.ErrorLoadingPosts(errorMessage)
                            }
                        }
                } else {
                    getPostsFromDatabase()
                        .flatMapObservable { postsFromDb ->
                            if (postsFromDb.isNullOrEmpty()) {
                                Observable.just(PostPartialState.NoInternetConnection)
                            } else {
                                Observable.merge(
                                    Observable.just(PostPartialState.LoadingPosts),
                                    Observable.just(
                                        PostPartialState.LoadedPostsFromDatabase(
                                            postsFromDb
                                        )
                                    )
                                )
                            }
                        }
                }
            }
    }

    private fun savePostsToDatabasePartialState(posts: List<Post>): Observable<PostPartialState> {
        return postLocalRepository.insertAll(*posts.toTypedArray())
            .flatMapObservable { ids ->
                if (ids.isNullOrEmpty()) {
                    Observable.just(PostPartialState.NoPostsAddedToDatabase)
                } else {
                    Observable.just(PostPartialState.PostsSavedToDatabase)
                }
            }.onErrorReturn { throwable ->
                throwable.message?.let { errorMessage ->
                    PostPartialState.ErrorSavingPostsInDatabase(errorMessage)
                }
            }
    }

    private fun getPostsFromDatabase(): Single<List<Post>> {
        return postLocalRepository.getAll()
    }

    fun generateSwipeToRefreshPartialState(): Observable<PostPartialState> {
        return Observable.merge(
            Observable.just(PostPartialState.SwipeToRefresh),
            generateInitialPartialState()
        )
    }

    fun generatePostClickPartialState(post: Post?): Observable<PostPartialState> {
        post?.let {
            postLocalRepository.setCurrentPost(it)
        }
        return Observable.just(PostPartialState.PostClicked)
    }

    fun generateOnPausePartialState(): Observable<PostPartialState> {
        return Observable.just(PostPartialState.OnPause)
    }

    fun generateLoadPostsFromStatePartialState(posts: List<Post>): Observable<PostPartialState> {
        val removedPost = postLocalRepository.getDeletedPost()
        val newPostList = posts.toMutableList()
        if (removedPost != null) {
            val removedPostPosition = posts.indexOf(removedPost)
            newPostList.removeAt(removedPostPosition)
            postLocalRepository.setDeletedPost(null)
        }

        return Observable.merge(
            Observable.just(PostPartialState.LoadingPosts),
            Observable.just(PostPartialState.LoadedPostsFromState(newPostList))
        )
    }

    fun generateStartServiceIntent(): Observable<PostPartialState> {
        return Observable.just(PostPartialState.StartDatabaseService)
    }
}
