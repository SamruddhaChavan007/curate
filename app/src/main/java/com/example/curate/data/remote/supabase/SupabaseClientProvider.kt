package com.example.curate.data.remote.supabase

import com.example.curate.core.config.AppConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupabaseClientProvider @Inject constructor() {
    fun create(): SupabaseClient {
        check(AppConfig.supabaseUrl.isNotBlank()) {
            "SUPABASE_URL is missing. Set DEV_BASE_URL, STAGING_BASE_URL, or PRODUCTION_BASE_URL in local.properties."
        }
        check(AppConfig.supabaseAnonKey.isNotBlank()) {
            "SUPABASE_ANON_KEY is missing. Set it in local.properties."
        }

        Timber.d("Creating Supabase client")

        return createSupabaseClient(
            supabaseUrl = AppConfig.supabaseUrl,
            supabaseKey = AppConfig.supabaseAnonKey
        ) {
            install(Auth)
            install(Postgrest)
            install(Realtime)
        }
    }
}
