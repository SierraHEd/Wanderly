package com.example.csc490group3.supabase

import com.example.csc490group3.BuildConfig
import com.example.csc490group3.model.event
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

    /**
     * Inserts a record into the specified table in the Supabase database.
     *
     * This function wraps the record in a list (as required by the Supabase insert API)
     * and performs the insertion on the IO dispatcher.
     *
     * @param tableName The name of the table where the record should be inserted.
     * @param record The record to insert. This must be a serializable object.
     * @return Returns true if the record was inserted successfully, false if an error occurred.
     */
    suspend inline fun <reified T : Any> addRecord(tableName: String, record: T): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val response = supabase.from(tableName).insert(listOf(record))
                println("Record inserted successfully.")
                true

            } catch (e: Exception) {
                println("Error inserting record: ${e.localizedMessage}")
                false
            }
        }
    }

    /**
     * Fetches a private user from the "private_users" table based on the provided email address.
     *
     * This function filters the table for records where the "email" column matches the given email,
     * and decodes the response into a [privateUser] object.
     *
     * @param email The email address of the user to fetch.
     * @return Returns a [privateUser] object if a matching record is found, or null if no match is found or an error occurs.
     */
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

    /**
     * Deletes an event from the "events" table based on the provided event ID.
     *
     * This function filters the "events" table for the record with the matching "id"
     * and deletes it, decoding the result into an [event] object.
     *
     * @param id The unique identifier of the event to delete.
     * @return Returns an [event] object representing the deleted event if successful, or null if an error occurs.
     */
    suspend fun deleteEvent(id: Int): event? {
        return withContext(Dispatchers.IO) {
            try {
                supabase.from("events").delete {
                    select()
                    filter {
                        eq("id", id)
                    }
                }.decodeSingle<event>()

            }catch(e: Exception) {
                println("Error deleting event: ${e.localizedMessage}")
                null
            }
        }
    }

}