package com.example.curate.data.repository

import com.example.curate.core.config.AppConfig
import com.example.curate.domain.repository.AuthRepository
import io.github.jan.supabase.SupabaseClient
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val supabaseClient: SupabaseClient
) : AuthRepository {
    override fun isSupabaseConfigured(): Boolean {
        return AppConfig.supabaseUrl.isNotBlank() &&
            AppConfig.supabaseAnonKey.isNotBlank() &&
            supabaseClient.supabaseUrl.isNotBlank()
    }
}
