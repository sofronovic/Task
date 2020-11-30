package com.nsofronovic.task.network

import com.nsofronovic.task.model.Post
import com.nsofronovic.task.model.User
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface PostApi {

    @GET("/posts")
    fun getPosts(): Observable<List<Post>>

    @GET("/users/{userId}")
    fun getUser(@Path("userId") userId: Int): Observable<User>
}
