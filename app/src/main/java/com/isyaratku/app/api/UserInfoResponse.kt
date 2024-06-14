package com.isyaratku.app.api

import com.google.gson.annotations.SerializedName

data class UserInfoResponse(

	@field:SerializedName("user")
	val user: User? = null
)

data class User(

	@field:SerializedName("createdAt")
	val createdAt: CreatedAt? = null,

	@field:SerializedName("url_photo")
	val urlPhoto: String? = null,

	@field:SerializedName("asl_score")
	val aslScore: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("bisindo_score")
	val bisindoScore: String? = null,

	@field:SerializedName("username")
	val username: String? = null
)

data class CreatedAt(

	@field:SerializedName("_nanoseconds")
	val nanoseconds: Int? = null,

	@field:SerializedName("_seconds")
	val seconds: Int? = null
)
