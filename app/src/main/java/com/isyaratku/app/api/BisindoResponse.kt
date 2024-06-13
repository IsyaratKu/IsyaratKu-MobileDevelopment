package com.isyaratku.app.api

import com.google.gson.annotations.SerializedName

data class BisindoResponse(

	@field:SerializedName("new_bisindo_score")
	val newBisindoScore: String? = null,

	@field:SerializedName("message")
	val message: String? = null
)
