package com.isyaratku.app.api

import com.google.gson.annotations.SerializedName

data class AslLeaderboardResponse(

	@field:SerializedName("users")
	val users: List<UsersItem?>? = null
)

data class UsersItem(

	@field:SerializedName("url_photo")
	val urlPhoto: String? = null,

	@field:SerializedName("asl_score")
	val aslScore: String? = null,

	@field:SerializedName("username")
	val username: String? = null
)
