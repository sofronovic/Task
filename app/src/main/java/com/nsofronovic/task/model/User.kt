package com.nsofronovic.task.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User (
	@PrimaryKey
	val id : Int,

	@ColumnInfo(name = "user_name")
	val name : String,

	@ColumnInfo(name ="user_email")
	val email : String
)
