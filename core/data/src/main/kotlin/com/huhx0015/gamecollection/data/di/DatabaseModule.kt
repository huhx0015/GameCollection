package com.huhx0015.gamecollection.data.di

import android.content.Context
import androidx.room.Room
import com.huhx0015.gamecollection.database.GameCollectionDatabase
import com.huhx0015.gamecollection.database.dao.OwnedGameDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideGameCollectionDatabase(
        @ApplicationContext context: Context,
    ): GameCollectionDatabase =
        Room.databaseBuilder(
            context,
            GameCollectionDatabase::class.java,
            "game_collection.db",
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideOwnedGameDao(db: GameCollectionDatabase): OwnedGameDao = db.ownedGameDao()
}
