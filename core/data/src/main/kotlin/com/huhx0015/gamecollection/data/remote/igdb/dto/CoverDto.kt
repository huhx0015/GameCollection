package com.huhx0015.gamecollection.data.remote.igdb.dto

import com.squareup.moshi.Json

data class CoverDto(
    @Json(name = "image_id") val imageId: String?,
)
