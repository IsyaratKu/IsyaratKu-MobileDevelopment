package com.isyaratku.app.api

import com.google.gson.annotations.SerializedName

data class UploadPhotoResponse(

	@field:SerializedName("url_photo")
	val urlPhoto: String? = null,

	@field:SerializedName("message")
	val message: String? = null
)
