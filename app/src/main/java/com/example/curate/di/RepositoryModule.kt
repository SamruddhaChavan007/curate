package com.example.curate.di

import com.example.curate.data.repository.AuthRepositoryImpl
import com.example.curate.data.repository.CollectionRepositoryImpl
import com.example.curate.data.repository.CurateRepositoryImpl
import com.example.curate.data.repository.WallpaperRepositoryImpl
import com.example.curate.domain.repository.AuthRepository
import com.example.curate.domain.repository.CollectionRepository
import com.example.curate.domain.repository.CurateRepository
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
    abstract fun bindCollectionRepository(
        implementation: CollectionRepositoryImpl
    ): CollectionRepository
}
