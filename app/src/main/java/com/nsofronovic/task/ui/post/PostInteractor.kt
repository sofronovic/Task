package com.nsofronovic.task.ui.post

import com.nsofronovic.task.repository.PostRepository
import io.reactivex.Observable

class PostInteractor(private val postRepository: PostRepository) {
    fun generateInitialPartialState(): Observable<PostPartialState> {
        return postRepository.getPosts()
            .flatMap { posts ->
                Observable.merge(
                    Observable.just(PostPartialState.LoadingPostsPartialState),
                    Observable.just(
                        (PostPartialState.LoadedPostsPartialState(
                            posts
                        ))
                    )
                )
            }.onErrorReturnItem(PostPartialState.ErrorLoadingPostsPartialState)
    }
}
