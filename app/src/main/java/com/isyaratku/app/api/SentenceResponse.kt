package com.isyaratku.app.api

import com.google.gson.annotations.SerializedName

data class SentenceResponse(

	@field:SerializedName("sentence")
	val sentence: String? = null,

	@field:SerializedName("score")
	val score: Int? = null
)
