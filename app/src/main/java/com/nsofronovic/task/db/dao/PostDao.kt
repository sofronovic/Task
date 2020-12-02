package com.nsofronovic.task.db.dao

import androidx.room.*
import com.nsofronovic.task.model.Post
import io.reactivex.Maybe
import io.reactivex.Single

/**
 * PostDao contains the methods used for accessing the database
 *
 **/
@Dao
interface PostDao {
    @Query("SELECT * FROM post")
    fun getAll(): Single<List<Post>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg posts: Post): Single<List<Long>>

    @Delete
    fun delete(post: Post): Single<Int>

    @Query("DELETE FROM post")
    fun deleteAll(): Single<Int>
}