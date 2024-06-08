package com.isyaratku.app.api

import com.google.gson.annotations.SerializedName

data class LeaderboardResponse(

	@field:SerializedName("users")
	val users: List<UsersItem?>? = null
)

data class UsersItem(

	@field:SerializedName("score")
	val score: Int? = null,

	@field:SerializedName("url_photo")
	val urlPhoto: String? = null,

	@field:SerializedName("username")
	val username: String? = null
)
