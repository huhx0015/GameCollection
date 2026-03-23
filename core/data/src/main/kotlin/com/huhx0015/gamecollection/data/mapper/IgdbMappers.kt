package com.huhx0015.gamecollection.data.mapper

import com.huhx0015.gamecollection.data.remote.igdb.IgdbImageUrl
import com.huhx0015.gamecollection.data.remote.igdb.dto.GameDetailsDto
import com.huhx0015.gamecollection.data.remote.igdb.dto.GameListDto
import com.huhx0015.gamecollection.data.remote.igdb.dto.GenreDto
import com.huhx0015.gamecollection.data.remote.igdb.dto.PlatformDto
import com.huhx0015.gamecollection.database.entity.OwnedGameEntity
import com.huhx0015.gamecollection.domain.model.GameDetails
import com.huhx0015.gamecollection.domain.model.GamePlatform
import com.huhx0015.gamecollection.domain.model.GameSummary
import com.huhx0015.gamecollection.domain.model.Genre
import com.huhx0015.gamecollection.domain.model.OwnedGame

fun PlatformDto.toDomain(): GamePlatform = GamePlatform(
    id = id,
    name = name,
    slug = slug,
    summary = summary,
)

fun GenreDto.toDomain(): Genre = Genre(id = id, name = name)

fun GameListDto.toDomain(): GameSummary = GameSummary(
    id = id,
    name = name,
    coverImageUrl = IgdbImageUrl.coverSmall(cover?.imageId),
    summary = summary,
    genreNames = genres?.mapNotNull { it.name } ?: emptyList(),
)

fun GameDetailsDto.toDomain(): GameDetails = GameDetails(
    id = id,
    name = name,
    summary = summary,
    storyline = storyline,
    coverImageUrl = IgdbImageUrl.coverBig(cover?.imageId),
    screenshotUrls = screenshots?.mapNotNull { IgdbImageUrl.screenshot(it.imageId) } ?: emptyList(),
    genreNames = genres?.mapNotNull { it.name } ?: emptyList(),
    firstReleaseDate = firstReleaseDate,
)

fun OwnedGameEntity.toDomain(): OwnedGame = OwnedGame(
    igdbGameId = igdbGameId,
    platformId = platformId,
    title = title,
    coverUrl = coverUrl,
    addedAtEpochMillis = addedAtEpochMillis,
)

fun OwnedGame.toEntity(): OwnedGameEntity = OwnedGameEntity(
    igdbGameId = igdbGameId,
    platformId = platformId,
    title = title,
    coverUrl = coverUrl,
    addedAtEpochMillis = addedAtEpochMillis,
)
