package com.huhx0015.gamecollection.data.remote.igdb

import com.huhx0015.gamecollection.data.remote.igdb.dto.GameDetailsDto
import com.huhx0015.gamecollection.data.remote.igdb.dto.GameListDto
import com.huhx0015.gamecollection.data.remote.igdb.dto.GenreDto
import com.huhx0015.gamecollection.data.remote.igdb.dto.PlatformDto
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface IgdbApi {

    @POST("platforms")
    suspend fun platforms(@Body body: RequestBody): Response<List<PlatformDto>>

    @POST("games")
    suspend fun games(@Body body: RequestBody): Response<List<GameListDto>>

    @POST("games")
    suspend fun gameDetails(@Body body: RequestBody): Response<List<GameDetailsDto>>

    @POST("genres")
    suspend fun genres(@Body body: RequestBody): Response<List<GenreDto>>
}
