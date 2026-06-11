package com.example.curate.di

import com.example.curate.data.remote.supabase.auth.SupabaseAuthGateway
import com.example.curate.data.remote.supabase.auth.SupabaseAuthGatewayImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SupabaseAuthGatewayModule {
    @Binds
    @Singleton
    abstract fun bindSupabaseAuthGateway(
        implementation: SupabaseAuthGatewayImpl
    ): SupabaseAuthGateway
}
