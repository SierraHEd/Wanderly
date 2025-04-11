package com.example.csc490group3.supabase

import com.example.csc490group3.model.Category
import com.example.csc490group3.model.Event
import com.example.csc490group3.model.IndividualUser
import com.example.csc490group3.model.User
import com.example.csc490group3.supabase.SupabaseManagement.DatabaseManagement.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

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

    //////////////////
    //EVENT FUNCTIONS
    /////////////////
    /**
     * Inserts a event into the event table in the Supabase database.
     *
     *
     * @param event the event object to be inserted
     * @return returns the event id of teh added event or -1 if an error occurred
     */
    suspend fun addEvent(event: Event): Int{
        return withContext(Dispatchers.IO) {
            try {
                val response = postgrest.from("events").insert(event){
                    select()
                }.decodeSingle<Event>()
                return@withContext response?.id!!
            } catch (e: Exception) {
                println("Error inserting event: ${e.localizedMessage}")
                return@withContext -1
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
    suspend fun removeEvent(id: Int): Event? {
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

                postgrest.from("events").update {  }
                true
            } catch (e: Exception) {
                println("Error inserting record: ${e.localizedMessage}")
                false
            }
        }
    }
    suspend fun unregisterEvent(event: Event, user: User): Boolean{
        return withContext(Dispatchers.IO) {
            val userID = user.id
            val eventID = event.id
            try {
                postgrest.from("user_events").delete {
                    select()
                    filter {
                        userID?.let { eq("user_id", it) }
                        eventID?.let { eq("event_id", it) }
                    }
                }
                println("Successfully unregistered from event: ${event.eventName}")
                true

            }catch(e: Exception) {
                println("Error unregistering: ${e.localizedMessage}")
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
    /////////////////
    //USER FUNCTIONS
    ////////////////
    /**
     * Fetches a private user from the "private_users" table based on the provided email address.
     *
     * This function filters the table for records where the "email" column matches the given email,
     * and decodes the response into a [IndividualUser] object.
     *
     * @param email The email address of the user to fetch.
     * @return Returns a [IndividualUser] object if a matching record is found, or null if no match is found or an error occurs.
     */
    suspend fun getPrivateUser(email: String): IndividualUser? {
        return withContext(Dispatchers.IO) {
            try{
                postgrest.from("private_users").select {
                    filter {
                        eq("email", email)
                    }
                }.decodeSingle<IndividualUser>()
            }catch(e: Exception) {
                println("Error fetching user record: ${e.localizedMessage}")
                null
            }
        }
    }
    suspend fun getPrivateUser(id: Int): IndividualUser? {
        return withContext(Dispatchers.IO) {
            try{
                postgrest.from("private_users").select {
                    filter {
                        eq("id", id)
                    }
                }.decodeSingle<IndividualUser>()
            }catch(e: Exception) {
                println("Error fetching user record: ${e.localizedMessage}")
                null
            }
        }
    }
    /**
    *Fetches a list of all events that a certain user created
    *
    *@param userID the user id of the user who you want to retrieve the events for
    * @returns a list of all events that a user created
    */
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

    /////////////////////////
    //SEARCH FUNCTIONS
    ////////////////////////
    /*
    *Fetches a list of events tha match a user inputted search query
    *
    * @param query a search term
    * @returns a list of events that match the search query
     */
    suspend fun simpleSearch(query: String): List<Event>? {
        return withContext(Dispatchers.IO) {
            try {
                val params = JsonObject(mapOf("term" to JsonPrimitive(query)))

                val result = postgrest.rpc("search_events", params).decodeList<Event>()
                println(result)
                result

            }catch(e: Exception) {
                println("Error fetching events: ${e.localizedMessage}")
                null
            }
        }

    }
    /**
    *Fetches a list of users that match the quesry, matches will be done on the users first and last name as
    * well as email address
    * @param query search term that will try and match either first name last name or email
    * @return list of users
     */
    suspend fun userSearch(query: String): List<IndividualUser>? {
        return withContext(Dispatchers.IO) {
            try {
                val params = JsonObject(mapOf("term" to JsonPrimitive(query)))

                val result = postgrest.rpc("search_users", params).decodeList<IndividualUser>()
                println(result)
                result

            }catch(e: Exception) {
                println("Error fetching events: ${e.localizedMessage}")
                null
            }
        }
    }

    ////////////////////////
    //CATEGORY MANIPULATION
    ///////////////////////

    suspend fun getCategories(id: Int, tableName: String): List<Category>?{
        return withContext(Dispatchers.IO) {
            try {
                val params = buildJsonObject {
                    put("id", id)
                    put("table_name", tableName)
                }

                val result = postgrest.rpc("get_categories", params).decodeList<Category>()
                result

            }catch(e: Exception) {
                println("Error fetching categories: ${e.localizedMessage}")
                null
            }
        }
    }

    suspend fun deleteCategories(id: Int, tableName: String){
        return withContext(Dispatchers.IO) {

            val idString = when (tableName) {
                "event_categories" -> "event_id"
                "user_categories" -> "user_id"
                else -> {
                    println("ERROR - UNRECOGNIZED TABLE")
                    null
                }
            }
            try {
                postgrest.from(tableName).delete {
                    select()
                    filter {
                        if (idString != null) {
                            eq(idString, id)
                        }
                    }
                }
            }catch(e: Exception) {
                println("Error deleting categories: ${e.localizedMessage}")
                null
            }
        }

    }
    /**
     * will form a relationship between categories and either an event or user in the DB
     *
     * @param categories a list of categories you cant to add to the user/event
     * @param id ID number of the event or user you are making the relationship with
     * @param tableName either event_categories or user_categories depending on what relationship is being made
     */
    suspend fun addCategoryRelationship(categories: List<Category>, tableName: String, id:Int){
        return withContext(Dispatchers.IO) {
            try {
                val idString = when (tableName) {
                    "event_categories" -> "event_id"
                    "user_categories" -> "user_id"
                    else -> {
                        println("ERROR - UNRECOGNIZED TABLE")
                        null
                    }
                }

                val joinRecords = categories.map{ category ->
                    mapOf("category_id" to category.id, idString to id)
                }

                val response = postgrest.from(tableName).insert(joinRecords)
                println("Categories Inserted Successfully")

            } catch (e: Exception) {
                println("Error inserting Category: ${e.localizedMessage}")
                false
            }
        }

    }
    /**
    *Fetches a list of events to suggest to a user matching their set categories with event set categories
    *
    *
    * @param userID the id of the user who you want to fetch the event suggestions for
    * @return a list of events
     */
    suspend fun getAllSuggestedEvents(userID:Int): List<Event>?{
        return withContext(Dispatchers.IO) {
            try {
                val userID = JsonObject(mapOf("user_id" to JsonPrimitive(userID)))

                val result = postgrest.rpc("all_suggested_events", userID).decodeList<Event>()
                println("Suggested events fetched successfully")
                result
            }catch(e: Exception) {
                println("Error fetching suggested events: ${e.localizedMessage}")
                null
            }
        }
    }

    //////////////////
    //MEDIA FUNCTIONS
    /////////////////
    suspend fun updateEventPhoto(eventId: Int, photoUrl: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                postgrest.from("events").update({
                    set("photo_url", photoUrl)
                }) {
                    filter { eq("id", eventId) }
                }
                println("Event photo updated successfully.")
                true
            } catch (e: Exception) {
                println("Error updating event photo: ${e.localizedMessage}")
                false
            }
        }
    }


    suspend fun updateUserProfilePicture(userId: Int, photoUrl: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                postgrest.from("private_users").update({
                    set("profile_picture_url", photoUrl)
                }) {
                    filter { eq("id", userId) }
                }
                println("User profile picture updated successfully.")
                true
            } catch (e: Exception) {
                println("Error updating profile picture: ${e.localizedMessage}")
                false
            }
        }
    }
}

///////////////////
//FRIEND FUNCTIONS
//////////////////

/**
*Fetches a list of users that are friends with the inputted user id
*
* @param userID the user you want to retrieve all the friends for
* @return a list of users
*/
suspend fun getFriends(userID: Int): List<IndividualUser>? {
    return withContext(Dispatchers.IO) {
        try {
            val userID = JsonObject(mapOf("userid" to JsonPrimitive(userID)))

            val result = postgrest.rpc("get_friends", userID).decodeList<IndividualUser>()
            println(result)
            result
        }catch(e: Exception) {
            println("Error fetching events: ${e.localizedMessage}")
            null
        }
    }
}
/**
*Will allow a user to send a friend request to another user
*
* @param currentUser the user who is logged in
* @param userToFriend user they want to follow
*
* @return true if follow was successful false if not
 */
suspend fun friendRequest(currentUser: Int, userToFriend: Int): String? {
    return withContext(Dispatchers.IO) {
        try {
            val params = buildJsonObject {
                put("user_a", currentUser)
                put("user_b", userToFriend)
            }

            val result = postgrest.rpc("friend_request", params).decodeSingle<String>()
            result

        }catch(e: Exception) {
            println("Error sending friend request: ${e.localizedMessage}")
            null
        }
    }
}
/**
*Will allow a user to unfriend another user
*
* @param currentUser the user who is logged in
* @param userToUnfriend user they want to unfollow
*
* @return true if unfollow was successful false if not
 */
suspend fun unfriend(currentUser: Int, userToUnfriend: Int):String? {
    return withContext(Dispatchers.IO) {
        try {
            val params = buildJsonObject {
                put("user_a", currentUser)
                put("user_b", userToUnfriend)
            }

            val result = postgrest.rpc("unfriend", params).decodeSingle<String>()
            result

        }catch(e: Exception) {
            println("Error unfriending: ${e.localizedMessage}")
            null
        }
    }
}
/**
 *Will allow a user to accept or deny a friend request
 *
 * @param currentUser the user who is logged in
 * @param otherUser user whose request they want to accept or deny
 *
 * @return true if unfollow was successful false if not
 */
suspend fun acceptDenyFriendRequest(currentUser: Int, otherUser: Int, accept: Boolean): Boolean?{
    return withContext(Dispatchers.IO) {
        try {
            val params = buildJsonObject {
                put("user_a", currentUser)
                put("user_b", otherUser)
                put("accept", accept)
            }

            val result = postgrest.rpc("respond_friend_request", params).decodeSingle<Boolean>()
            result

        }catch(e: Exception) {
            println("Error responding to friend request: ${e.localizedMessage}")
            null
        }
    }
}

/**
 * Will allow us to fetch a list of incoming or outgoing friend requests
 *
 * @param user the user we would like to get the list of requests for
 * @param incoming boolean to describe which list we want to retrieve, true for incoming requests, false for outgoing
 * @return a list of users that this user is trying to friend request o has outgoing requests to
 */
suspend fun getPendingIncomingRequests(user: Int, incoming: Boolean):List<IndividualUser>?{
    return withContext(Dispatchers.IO) {
        try {
            val params = buildJsonObject {
                put("user_id", user)
                put("incoming", incoming)
            }

            val result = postgrest.rpc("get_friend_requests", params).decodeList<IndividualUser>()
            result

        }catch(e: Exception) {
            println("Error responding to friend request: ${e.localizedMessage}")
            null
        }
    }
}

























