package com.huhx0015.gamecollection.navigation

/** Central definitions for Navigation Compose routes and route builders. */
object Routes {
    const val GAMES_TAB = "games_tab"
    const val COLLECTION_TAB = "collection_tab"

    const val PLATFORM_SELECT = "games/platform"
    const val GAME_LIST = "games/list/{platformId}"
    const val GAME_DETAIL = "game_detail/{gameId}/{platformId}"

    const val COLLECTION_LIST = "collection/list"

    fun gameList(platformId: Long) = "games/list/$platformId"
    fun gameDetail(gameId: Long, platformId: Long) = "game_detail/$gameId/$platformId"
}
