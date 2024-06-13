package com.isyaratku.app.api

import com.google.gson.annotations.SerializedName

data class ASLResponse(

	@field:SerializedName("new_asl_score")
	val newAslScore: String? = null,

	@field:SerializedName("message")
	val message: String? = null
)
