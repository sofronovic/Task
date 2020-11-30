package com.nsofronovic.task.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nsofronovic.task.db.dao.PostDao
import com.nsofronovic.task.db.dao.UserDao
import com.nsofronovic.task.model.Post
import com.nsofronovic.task.model.User

@Database(entities = [Post::class, User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun userDao(): UserDao
}
