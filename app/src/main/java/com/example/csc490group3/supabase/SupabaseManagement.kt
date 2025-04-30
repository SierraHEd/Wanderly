package com.example.csc490group3.supabase

import com.example.csc490group3.BuildConfig
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.realtime.realtime
import io.github.jan.supabase.storage.Storage
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp

object SupabaseManagement {
    val supabase by lazy {
        createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_ANON_KEY

        ) {
            install(Postgrest)
            install(Storage)
            install(Auth)
            install(Realtime)

        }
    }
    object DatabaseManagement {
        val postgrest get() = supabase.postgrest
    }

    object AuthManagement {
        val auth get() = supabase.auth
    }

    object RealtimeManagment {
        val realtime get() = supabase.realtime
    }
}