package com.isyaratku.app.api

import com.google.gson.annotations.SerializedName

data class BisindoLeaderboardResponse(

	@field:SerializedName("users")
	val users: List<UsersItemBisindo?>? = null
)

data class UsersItemBisindo(

	@field:SerializedName("url_photo")
	val urlPhoto: String? = null,

	@field:SerializedName("bisindo_score")
	val bisindoScore: String? = null,

	@field:SerializedName("username")
	val username: String? = null
)
