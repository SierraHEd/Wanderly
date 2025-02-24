package com.example.csc490group3.supabase

import com.example.csc490group3.BuildConfig
import com.example.csc490group3.model.privateUser
import com.example.csc490group3.model.user
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer

object supabaseManagment {

    val supabase by lazy {
        createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_ANON_KEY
        ) {
            install(Postgrest)
        }
    }

    suspend inline fun <reified T : Any> addRecord(tableName: String, record: T): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val response = supabase.from(tableName)
                    .insert(listOf(record))
                println("Record inserted successfully.")
                true

            } catch (e: Exception) {
                println("Error inserting record: ${e.localizedMessage}")
                false
            }
        }
    }

    suspend fun getPrivateUser(email: String): privateUser? {
        return withContext(Dispatchers.IO) {
            try{
                supabase.from("private_users").select {
                    filter {
                        eq("email", email)
                    }
                }.decodeSingle<privateUser>()
            }catch(e: Exception) {
                println("Error fetching user record: ${e.localizedMessage}")
                null
            }

        }

    }
}