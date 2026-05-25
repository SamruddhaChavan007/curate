package com.example.curate.core.config

import com.example.curate.BuildConfig

object AppConfig {
    const val baseUrl: String = BuildConfig.BASE_URL
    const val supabaseUrl: String = BuildConfig.SUPABASE_URL
    const val supabaseAnonKey: String = BuildConfig.SUPABASE_ANON_KEY
    const val unsplashAccessKey: String = BuildConfig.UNSPLASH_ACCESS_KEY
}
