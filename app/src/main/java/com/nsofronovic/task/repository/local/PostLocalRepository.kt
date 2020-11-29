package com.nsofronovic.task.repository.local

import com.nsofronovic.task.db.dao.PostDao
import com.nsofronovic.task.model.Post
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

interface PostLocalRepository {
    fun getAll(): Single<List<Post>>
    fun insertAll(vararg posts: Post): Single<List<Long>>
    fun delete(post: Post)
}

class PostLocalRepositoryImpl(private val postDao: PostDao) : PostLocalRepository{

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

    override fun delete(post: Post) {
        postDao.delete(post)
    }

}