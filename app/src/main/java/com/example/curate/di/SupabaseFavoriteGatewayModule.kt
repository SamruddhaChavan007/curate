package com.example.curate.di

import com.example.curate.data.remote.supabase.favorite.SupabaseFavoriteGateway
import com.example.curate.data.remote.supabase.favorite.SupabaseFavoriteGatewayImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SupabaseFavoriteGatewayModule {

    @Binds
    @Singleton
    abstract fun bindSupabaseFavoriteGateway(
        impl: SupabaseFavoriteGatewayImpl
    ): SupabaseFavoriteGateway
}