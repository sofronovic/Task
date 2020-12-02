package com.nsofronovic.task.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.nsofronovic.task.model.Post
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
interface PostDao {
    @Query("SELECT * FROM post")
    fun getAll(): Single<List<Post>>

    @Insert
    fun insertAll(vararg posts: Post): Single<List<Long>>

    @Delete
    fun delete(post: Post): Single<Int>

    @Query("DELETE FROM post")
    fun deleteAll(): Single<Int>
}