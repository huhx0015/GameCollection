package com.huhx0015.gamecollection.domain.model

/**
 * Maps to IGDB [release_dates.region](https://api-docs.igdb.com/#release-date-region-enums) values.
 * Asia is a product grouping; we map to multiple IGDB region codes.
 */
enum class RegionFilter(val id: String, val igdbRegionIds: List<Int>) {
    ALL("all", emptyList()),
    NORTH_AMERICA("na", listOf(2)),
    EUROPE("eu", listOf(1)),
    JAPAN("jp", listOf(5)),
    ASIA("asia", listOf(6, 7, 8)),
}
