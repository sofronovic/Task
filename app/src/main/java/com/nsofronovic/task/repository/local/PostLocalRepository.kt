package com.nsofronovic.task.repository.local

import com.nsofronovic.task.db.dao.PostDao
import com.nsofronovic.task.model.Post
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/*
 * PostLocalRepository abstraction layer over database calls
 *
 **/
interface PostLocalRepository {
    fun getAll(): Single<List<Post>>
    fun insertAll(vararg posts: Post): Single<List<Long>>
    fun delete(post: Post): Single<Int>
    fun deleteAll(): Single<Int>

    fun setCurrentPost(post: Post)
    fun getCurrentPost(): Post

    fun getDeletedPost(): Post?
    fun setDeletedPost(post: Post?)
}

class PostLocalRepositoryImpl(private val postDao: PostDao) : PostLocalRepository {

    private lateinit var currentPost: Post

    private var deletedPost: Post? = null

    override fun getAll(): Single<List<Post>> {
        return postDao.getAll()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun insertAll(vararg posts: Post): Single<List<Long>> {
        return postDao.insertAll(*posts)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun delete(post: Post): Single<Int> {
        return postDao.delete(post)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun deleteAll(): Single<Int> {
        return postDao.deleteAll()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun setCurrentPost(post: Post) {
        currentPost = post
    }

    override fun getCurrentPost(): Post {
        return currentPost
    }

    override fun getDeletedPost(): Post? {
        return deletedPost
    }

    override fun setDeletedPost(post: Post?) {
        deletedPost = post
    }
}
