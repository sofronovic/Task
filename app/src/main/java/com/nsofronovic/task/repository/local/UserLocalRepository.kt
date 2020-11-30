package com.nsofronovic.task.repository.local

import com.nsofronovic.task.db.dao.UserDao
import com.nsofronovic.task.model.User
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

interface UserLocalRepository {
    fun getUserById(userId: Int): Maybe<User>
    fun insert(user: User): Single<Long>
    fun delete(user: User)
}

class UserLocalRepositoryImpl(private val userDao: UserDao) : UserLocalRepository {
    override fun getUserById(userId: Int): Maybe<User> {
        return userDao.getUserById(userId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun insert(user: User): Single<Long> {
        return userDao.insert(user)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun delete(user: User) {
        return userDao.delete(user)
    }
}
