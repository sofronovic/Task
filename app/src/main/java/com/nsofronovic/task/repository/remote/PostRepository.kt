package com.nsofronovic.task.repository.remote

import com.nsofronovic.task.model.Post
import com.nsofronovic.task.model.User
import com.nsofronovic.task.network.PostApi
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/*
 * PostRepository abstraction layer over API calls
 *
 **/
class PostRepository(private val postApi: PostApi) {

    fun getPosts(): Observable<List<Post>> {
        return postApi.getPosts()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}
