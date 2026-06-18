package com.example.curate.di

import com.example.curate.data.repository.AuthRepositoryImpl
import com.example.curate.data.repository.CurateRepositoryImpl
import com.example.curate.data.repository.SearchHistoryRepositoryImpl
import com.example.curate.data.repository.TopicsRepositoryImpl
import com.example.curate.data.repository.WallpaperRepositoryImpl
import com.example.curate.domain.repository.AuthRepository
import com.example.curate.domain.repository.CurateRepository
import com.example.curate.domain.repository.SearchHistoryRepository
import com.example.curate.domain.repository.TopicsRepository
import com.example.curate.domain.repository.WallpaperRepository
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
    abstract fun bindAuthRepository(
        implementation: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindCurateRepository(
        implementation: CurateRepositoryImpl
    ): CurateRepository

    @Binds
    @Singleton
    abstract fun bindWallpaperRepository(
        implementation: WallpaperRepositoryImpl
    ): WallpaperRepository

    @Binds
    @Singleton
    abstract fun bindTopicsRepository(
        implementation: TopicsRepositoryImpl
    ): TopicsRepository

    @Binds
    @Singleton
    abstract fun bindSearchHistoryRepository(
        implementation: SearchHistoryRepositoryImpl
    ): SearchHistoryRepository
}
