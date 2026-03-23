package com.huhx0015.gamecollection.data.di

import com.huhx0015.gamecollection.data.repository.CollectionRepositoryImpl
import com.huhx0015.gamecollection.data.repository.IgdbRepositoryImpl
import com.huhx0015.gamecollection.domain.repository.CollectionRepository
import com.huhx0015.gamecollection.domain.repository.IgdbRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindIgdbRepository(impl: IgdbRepositoryImpl): IgdbRepository

    @Binds
    @Singleton
    abstract fun bindCollectionRepository(impl: CollectionRepositoryImpl): CollectionRepository
}
