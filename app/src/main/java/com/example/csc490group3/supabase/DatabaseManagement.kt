package com.example.csc490group3.supabase

import com.example.csc490group3.model.Event
import com.example.csc490group3.model.PrivateUser
import com.example.csc490group3.model.User
import com.example.csc490group3.supabase.SupabaseManagement.DatabaseManagement.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.csc490group3.model.UserSession
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

object DatabaseManagement {

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
                val response = postgrest.from(tableName).insert(listOf(record))
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
     * and decodes the response into a [PrivateUser] object.
     *
     * @param email The email address of the user to fetch.
     * @return Returns a [PrivateUser] object if a matching record is found, or null if no match is found or an error occurs.
     */
    suspend fun getPrivateUser(email: String): PrivateUser? {
        return withContext(Dispatchers.IO) {
            try{
                postgrest.from("private_users").select {
                    filter {
                        eq("email", email)
                    }
                }.decodeSingle<PrivateUser>()
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
     * and deletes it, decoding the result into an [Event] object.
     *
     * @param id The unique identifier of the event to delete.
     * @return Returns an [Event] object representing the deleted event if successful, or null if an error occurs.
     */
    suspend fun deleteEvent(id: Int): Event? {
        return withContext(Dispatchers.IO) {
            try {
                postgrest.from("events").delete {
                    select()
                    filter {
                        eq("id", id)
                    }
                }.decodeSingle<Event>()

            }catch(e: Exception) {
                println("Error deleting event: ${e.localizedMessage}")
                null
            }
        }
    }
    /**
     * Fetches all events from the "events" table.
     *
     * @return A list of [Event] objects if successful, or null if an error occurred.
     */
    suspend fun getAllEvents(): List<Event>? {
        return withContext(Dispatchers.IO) {
            try{
                val result = postgrest.from("events")
                    .select()
                    .decodeList<Event>()
                result
            }catch(e: Exception) {
                println("Error fetching events: ${e.localizedMessage}")
                null
            }
        }
    }

    /**
     * Registers a user to an event by adding their id and the event id to the "user_events" table
     *
     * @return Returns true if the record was inserted successfully, false if an error occurred.
     */
    suspend fun registerEvent(event: Event, currentUser: User?) : Boolean{
        return withContext(Dispatchers.IO) {
            try {
                val response = postgrest.from("user_events").insert(mapOf("user_id" to currentUser?.id, "event_id" to event.id))
                println("Record inserted successfully.")
                true

            } catch (e: Exception) {
                println("Error inserting record: ${e.localizedMessage}")
                false
            }
        }
    }

    /**
     * Fetches a list of all events a particular user is registered for
     *
     * @return A list of [Event] objects if successful, or null if an error occurred.
     */
    suspend fun getUserEvents(userID: Int): List<Event>? {
        return withContext(Dispatchers.IO) {
            try {
                if(userID == null) {
                    println("User not logged in")
                    return@withContext null
                }
                val params = JsonObject(mapOf("userid" to JsonPrimitive(userID)))

                    val result = postgrest.rpc("getuserevents", params).decodeList<Event>()
                    result

            }catch(e: Exception) {
                println("Error fetching events: ${e.localizedMessage}")
                null
            }
        }
    }
    suspend fun getUserCreatedEvents(userID: Int): List<Event>? {
        return withContext(Dispatchers.IO) {
            try{
                val result = postgrest.from("events")
                    .select {
                        filter {
                            eq("created_by", userID)
                        }
                    }.decodeList<Event>()
                result.forEach { event ->
                    println("Event ID: ${event.id}")}

                result
            }catch(e: Exception) {
                println("Error fetching events: ${e.localizedMessage}")
                null
            }
        }
    }



}