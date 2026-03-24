package com.huhx0015.gamecollection.data.remote.igdb

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

/** Constructs Apicalypse query bodies for platforms, genres, game lists, and game details. */
object IgdbQueryBuilder {

    private val textPlain = "text/plain; charset=utf-8".toMediaType()

    fun platformsRequestBody(search: String?, offset: Int, limit: Int): RequestBody =
        buildString {
            append("fields id,name,slug,summary; ")
            if (!search.isNullOrBlank()) {
                append("search \"${escape(search)}\"; ")
            }
            append("limit $limit; offset $offset; ")
        }.toRequestBody(textPlain)

    fun platformsByIdsRequestBody(ids: Set<Long>): RequestBody {
        require(ids.isNotEmpty()) { "ids must not be empty" }
        val idList = ids.sorted().joinToString(",")
        return "fields id,name,slug,summary; where id = ($idList); limit ${ids.size}; "
            .toRequestBody(textPlain)
    }

    fun genresRequestBody(): RequestBody =
        "fields id,name; limit 500; ".toRequestBody(textPlain)

    fun gamesRequestBody(
        platformId: Long,
        offset: Int,
        limit: Int,
        searchQuery: String?,
        genreIds: Set<Long>,
    ): RequestBody {
        val sb = StringBuilder()
        sb.append("fields id,name,summary,genres.name,cover.image_id; ")
        val filters = mutableListOf<String>()
        filters.add("platforms = ($platformId)")
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
