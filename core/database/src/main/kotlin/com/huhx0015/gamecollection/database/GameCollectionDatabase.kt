package com.huhx0015.gamecollection.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.huhx0015.gamecollection.database.dao.OwnedGameDao
import com.huhx0015.gamecollection.database.entity.OwnedGameEntity

/** Room database for locally stored owned games. */
@Database(
    entities = [OwnedGameEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class GameCollectionDatabase : RoomDatabase() {
    abstract fun ownedGameDao(): OwnedGameDao
}
