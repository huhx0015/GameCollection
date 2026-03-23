package com.huhx0015.gamecollection.data.remote.igdb

import com.huhx0015.gamecollection.domain.model.RegionFilter
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

object IgdbQueryBuilder {

    private val textPlain = "text/plain; charset=utf-8".toMediaType()

    fun platformsRequestBody(search: String?): RequestBody =
        buildString {
            append("fields id,name,slug,summary; ")
            if (!search.isNullOrBlank()) {
                append("search \"${escape(search)}\"; ")
            }
            append("limit 100; ")
        }.toRequestBody(textPlain)

    fun genresRequestBody(): RequestBody =
        "fields id,name; limit 500; ".toRequestBody(textPlain)

    fun gamesRequestBody(
        platformId: Long,
        offset: Int,
        limit: Int,
        searchQuery: String?,
        region: RegionFilter,
        genreIds: Set<Long>,
    ): RequestBody {
        val sb = StringBuilder()
        sb.append("fields id,name,summary,genres.name,cover.image_id; ")
        val filters = mutableListOf<String>()
        filters.add("platforms = ($platformId)")
        if (region != RegionFilter.ALL && region.igdbRegionIds.isNotEmpty()) {
            val regionClause = region.igdbRegionIds
                .joinToString(" | ") { rid -> "release_dates.region = $rid" }
            filters.add("($regionClause)")
        }
        if (genreIds.isNotEmpty()) {
            val g = genreIds.joinToString(",")
            filters.add("genres = ($g)")
        }
        sb.append("where ${filters.joinToString(" & ")}; ")
        if (!searchQuery.isNullOrBlank()) {
            sb.append("search \"${escape(searchQuery)}\"; ")
        }
        sb.append("limit $limit; offset $offset; ")
        return sb.toString().toRequestBody(textPlain)
    }

    fun gameDetailsRequestBody(gameId: Long): RequestBody =
        """
        fields id,name,summary,storyline,genres.name,cover.image_id,screenshots.image_id,first_release_date;
        where id = $gameId;
        limit 1;
        """.trimIndent().toRequestBody(textPlain)

    private fun escape(s: String): String = s.replace("\\", "\\\\").replace("\"", "\\\"")
}
