package com.nsofronovic.task.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.nsofronovic.task.model.User
import io.reactivex.Maybe
import io.reactivex.Single

/**
 * UserDao contains the methods used for accessing the database
 *
 **/
@Dao
interface UserDao {

    @Insert
    fun insert(user: User): Single<Long>

    @Query("SELECT * FROM user WHERE id = :userId")
    fun getUserById(userId: Int): Maybe<User>

    @Delete
    fun delete(user: User)
}
