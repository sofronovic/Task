package com.nsofronovic.task.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Post (
	@PrimaryKey
	val id : Int,

	@ColumnInfo(name = "user_id")
	val userId : Int,

	@ColumnInfo(name = "post_title")
	val title : String,

	@ColumnInfo(name = "post_body")
	val body : String
)
