package com.huhx0015.gamecollection.data.remote.igdb

/** Builds CDN URLs for IGDB image ids (covers and screenshots). */
object IgdbImageUrl {
    fun coverSmall(imageId: String?): String? = build(imageId, "t_cover_small")
    fun coverBig(imageId: String?): String? = build(imageId, "t_cover_big")
    fun screenshot(imageId: String?): String? = build(imageId, "t_screenshot_med")

    private fun build(imageId: String?, size: String): String? {
        if (imageId.isNullOrBlank()) return null
        return "https://images.igdb.com/igdb/image/upload/$size/$imageId.jpg"
    }
}
