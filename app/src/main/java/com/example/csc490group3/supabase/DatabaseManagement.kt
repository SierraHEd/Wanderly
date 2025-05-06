package com.example.csc490group3.supabase

import android.util.Log
import androidx.annotation.IntegerRes
import com.example.csc490group3.model.Admin
import com.example.csc490group3.model.Category
import com.example.csc490group3.model.ConversationPreview
import com.example.csc490group3.model.Event
import com.example.csc490group3.model.IndividualUser

import com.example.csc490group3.model.Message

import com.example.csc490group3.model.Notification
import com.example.csc490group3.model.NotificationType

import com.example.csc490group3.model.Report
import com.example.csc490group3.model.UnreadCount
import com.example.csc490group3.model.User
import com.example.csc490group3.model.WaitList
import com.example.csc490group3.supabase.SupabaseManagement.DatabaseManagement.postgrest
import io.github.jan.supabase.postgrest.query.Order
import com.example.csc490group3.model.UserSession

import com.example.csc490group3.model.UserSession.currentUser
import com.example.csc490group3.supabase.DatabaseManagement.getEventById


import com.example.csc490group3.supabase.DatabaseManagement.getPrivateUser

import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import kotlinx.serialization.descriptors.PrimitiveKind


import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

import java.lang.Exception

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

                // Attempt to promote someone from the waitlist
                promoteFirstUserFromWaitlist(eventID!!)

                insertNotification( //Send user notification for unregistered event
                    Notification(
                        user_id = userID!!,
                        message = "You have unregistered from the event: ${event.eventName}",
                        is_read = false,
                        type = NotificationType.EVENT
                    )
                )

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

    suspend fun getEventById(eventId: Int): Event? {
        return withContext(Dispatchers.IO) {
            try {
                postgrest.from("events").select {
                    filter { eq("id", eventId) }
                    limit(1)
                }.decodeSingle<Event>()
            } catch (e: Exception) {
                println("Error fetching event by ID: ${e.localizedMessage}")
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

    suspend fun isUserPublicById(userId: Int): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val user = postgrest.from("private_users").select {
                    filter {
                        eq("id", userId)
                    }
                }.decodeSingle<IndividualUser>()

                user.public
            } catch (e: Exception) {
                println("Error checking user privacy by ID: ${e.localizedMessage}")
                false
            }
        }
    }

    suspend fun setUserPrivacy(userId: Int, isPublic: Boolean): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val response = postgrest.from("private_users")
                    .update(
                        mapOf("public" to isPublic)
                    ) {
                        filter {
                            eq("id", userId)
                        }
                    }

                 true // Returns true if the update succeeded
            } catch (e: Exception) {
                println("Error updating user privacy: ${e.localizedMessage}")
                false
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

    suspend fun reportEvent(event: Event, reason: String): Boolean {
        val reportType: Int = when (reason) {
            "Fake Event" -> 1
            "Dangerous Event" -> 2
            "Spam event" -> 3
            else -> {
                println("Invalid report reason: $reason")
                return false
            }
        }

        val report = Report(
            reported_By = UserSession.currentUser?.email ?: return false,
            reported_event_id = event.id?: return false,
            report_type = reportType,
            reported_Event = event.eventName?: return false
        )

        return withContext(Dispatchers.IO) {
            try {
                val inserted = SupabaseManagement.supabase
                    .from("reported_events")
                    .insert(report)
                    .decodeSingle<Report>()

                inserted != null
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }


    suspend fun getReportedEventsWithNames(): List<Pair<Report, String>> {
        return withContext(Dispatchers.IO) {
            try {
                val reports = SupabaseManagement.supabase
                    .from("reported_events")
                    .select()
                    .decodeList<Report>()

                val events = SupabaseManagement.supabase
                    .from("events")
                    .select()
                    .decodeList<Event>()

                reports.mapNotNull { report ->
                    val eventName = report.reported_Event // fallback directly from report
                    if (eventName.isNotEmpty()) {
                        Pair(report, eventName)
                    } else {
                        null
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }

    suspend fun deleteEvent(eventId: Int) {
        withContext(Dispatchers.IO) {
            try {
                SupabaseManagement.supabase
                    .from("events")
                    .delete {
                        filter { eq("id", eventId) }
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun dismissReport(reportId: Int) {
        withContext(Dispatchers.IO) {
            try {
                SupabaseManagement.supabase
                    .from("reported_events")
                    .delete {
                        filter { eq("report_id", reportId) }
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun isAdmin(email: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val result = SupabaseManagement.supabase
                    .from("admin")
                    .select {
                        filter {  eq("admin_email", email) }

                    }
                    .decodeList<Admin>()

                result.isNotEmpty()
            } catch (e: Exception) {
                Log.e("isAdmin", "Admin check failed: ${e.message}")
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

            val result = postgrest.rpc("friend_request", params).decodeList<String>()
            result[0]

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

            val result = postgrest.rpc("unfriend", params).decodeList<String>()
            result[0]

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

    /**
     * Will check what teh friendship status between two users is
     *
     * @param currentUser the user that is logged in
     * @param otherUser user you wan tot see if current user is friends with
     *
     * @returns the friendship status of two users as "accepted", "pending", "declined"
     */
    suspend fun checkFriendStatus(currentUser: Int, otherUser: Int): String?{
        return withContext(Dispatchers.IO) {
            try {
                val params = buildJsonObject {
                    put("user_a", currentUser)
                    put("user_b", otherUser)
                }

                // Get the raw JSON response as a string.
                val result = postgrest.rpc("get_friend_status", params).decodeList<String>()
                result[0]

            }catch(e: Exception) {
                println("Error getting status: ${e.localizedMessage}")
                null
            }
        }

    }

///////////////////
//WAITLIST FUNCTIONS
//////////////////

    suspend fun addUserToWaitingList(userId: Int, eventId: Int): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                postgrest.from("user_waitingList").insert(
                    mapOf(
                        "user_id" to userId,
                        "event_id" to eventId
                    )
                )
                println("Added to waiting list successfully.")
                true
            } catch (e: Exception) {
                println("Error inserting into waiting list: ${e.localizedMessage}")
                false
            }
        }
    }

    suspend fun isUserOnWaitingList(userID: Int, eventID: Int): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val result = postgrest.from("user_waitingList").select {
                    filter {
                        eq("user_id", userID)
                        eq("event_id", eventID)
                    }
                    limit(1)
                }.decodeList<WaitList>()

                return@withContext result.isNotEmpty()
            } catch (e: Exception) {
                println("Error checking waiting list: ${e.localizedMessage}")
                false
            }
        }
    }
/**
 * Check the user_waitList table for the oldest entry of a user for a specific event.
 * Adds user to user_events table for specific event.
 * Removes user from user_waitList table for specific event.
 *
 * @param eventId is Id passed when active user selects an event.
 *
 */
    private suspend fun promoteFirstUserFromWaitlist(eventId: Int) {
        return withContext(Dispatchers.IO) {
            try {
                val result = postgrest.from("user_waitingList").select {
                    filter { eq("event_id", eventId) }
                    order("created_at", Order.ASCENDING)
                    limit(1)
                }.decodeList<WaitList>() // Decode directly into a list of WaitList objects

                if (result.isNotEmpty()) {
                    val userId = result.first().user_id // Access user_id from the first WaitList object
                    // Insert the user into user_events table
                    postgrest.from("user_events").insert(
                        mapOf("user_id" to userId, "event_id" to eventId)
                    )

                    // Remove the user from the waitlist
                    postgrest.from("user_waitingList").delete {
                        filter {
                            eq("user_id", userId)
                            eq("event_id", eventId)
                        }
                    }
                    val event = getEventById(eventId)
                    // Send notification
                    if (event != null) {
                        val message = "You have been promoted from the waitlist to registered for: ${event.eventName}"
                        val notification = Notification(
                            user_id = userId,
                            message = message,
                            is_read = false,
                            type = NotificationType.EVENT
                        )
                        insertNotification(notification)
                    }

                    println("User $userId promoted from waitlist to registered.")
                } else {
                    println("No users on the waitlist to promote.")
                }
            } catch (e: Exception) {
                println("Error promoting user from waitlist: ${e.localizedMessage}")
            }
        }
    }
/**
 * Delete record the user_waitList table for user and event.
 *
 * Removes user from user_waitList table for specific event.
 *
 * @param userId is the user that will be removed from table.
 * @param eventId is Id passed when active user selects an event.
 *
 */
suspend fun removeUserFromWaitingList(userId: Int, eventId: Int): Boolean {
    return withContext(Dispatchers.IO) {
        try {
            postgrest.from("user_waitingList").delete {
                filter {
                    eq("user_id", userId)
                    eq("event_id", eventId)
                }
            }
            println("Removed from waiting list successfully.")
            true
        } catch (e: Exception) {
            println("Error removing from waiting list: ${e.localizedMessage}")
            false
        }
    }
}
///////////////////////
//MESSAGING  FUNCTIONS
//////////////////////

suspend fun getConversations(userID: Int): List<ConversationPreview>? {
    return withContext(Dispatchers.IO) {
        try {
            val userID = JsonObject(mapOf("user_id" to JsonPrimitive(userID)))

            val result = postgrest.rpc("get_conversations_for_user", userID).decodeList<ConversationPreview>()
            result
        }catch(e: Exception) {
            println("Error fetching conversations: ${e.localizedMessage}")
            null
        }
    }
}

suspend fun getConversationWithUser(otherUserID:Int): List<Message>{
    return withContext(Dispatchers.IO) {
        try {
            val params = buildJsonObject {
                put("user1", currentUser?.id)
                put("user2", otherUserID)
            }

            // Get the raw JSON response as a string.
            val result = postgrest.rpc("get_conversation_between_users", params).decodeList<Message>()
            print(result)
            result

        }catch(e: Exception) {
            println("Error retrieving messages: ${e.localizedMessage}")
            listOf<Message>()
        }
    }
}
suspend fun sendChatMessage(messageText: String, otherUserID: Int){
    return withContext(Dispatchers.IO) {
        try {
            val params = buildJsonObject {
                put("sender_id", currentUser?.id)
                put("receiver_id", otherUserID)
                put("content", messageText)
            }

            // Get the raw JSON response as a string.
            val result = postgrest.rpc("send_message", params)

        }catch(e: Exception) {
            println("Error sending message: ${e.localizedMessage}")
        }
    }
}

suspend fun markRead(receiverId: Int, senderId: Int) {
    return withContext(Dispatchers.IO) {
        try {
            val params = buildJsonObject {
                put("p_receiver_id", receiverId)
                put("p_sender_id", senderId)
            }

            // Get the raw JSON response as a string.
            val result = postgrest.rpc("mark_conversation_as_read", params)

        }catch(e: Exception) {
            println("Error marking as read: ${e.localizedMessage}")
        }
    }
}

suspend fun getTotalUnread(userId: Int): Int = withContext(Dispatchers.IO) {
    try {
        val params = buildJsonObject { put("user_id", userId) }

        // RPC now returns an array of { "count": Int }
        val results: List<UnreadCount> = postgrest
            .rpc("count_unread_messages", params)
            .decodeList<UnreadCount>()

        // Take the first element’s count, or 0 if none
        results.firstOrNull()?.count ?: 0

    } catch (e: Exception) {
        println("Error fetching unread count: ${e.localizedMessage}")
        0
    }
}
suspend fun getUnreadCountBetween(receiverId: Int,   senderId: Int): Int {
    return withContext(Dispatchers.IO) {
        try {
            val params = buildJsonObject {
                put("p_receiver_id", receiverId)
                put("p_sender_id", senderId)
            }
            val rows: List<UnreadCount> = postgrest
                .rpc("count_unread_between", params)
                .decodeList<UnreadCount>()
            rows.firstOrNull()?.count ?: 0
        } catch (e: Exception) {
            println("Error fetching unread‐between count: ${e.localizedMessage}")
            0
        }
    }
}

suspend fun sendEvent(eventId: Int, otherUserID: Int): Boolean{
    return withContext(Dispatchers.IO) {
        try {
            val messageText = "Check out this event:"
            val params = buildJsonObject {
                put("p_sender_id", currentUser?.id)
                put("p_receiver_id", otherUserID)
                put("p_content", messageText)
                put("p_event_id", eventId)
            }

            // Get the raw JSON response as a string.
            postgrest.rpc("send_event_message", params)
            true

        }catch(e: Exception) {
            println("Error sending event message: ${e.localizedMessage}")
        }
        false
    }
}


///////////////////
//NOTIFICATION FUNCTIONS
/////////////////

suspend fun getUnreadNotifications(userId: Int): List<Notification> {
    return withContext(Dispatchers.IO) {
        try {
            val result = postgrest.from("user_notifications").select {
                filter {
                    eq("user_id", userId)
                    eq("is_read", false)
                }
                order("created_at", Order.DESCENDING)  // Sorting by the latest notifications
            }.decodeList<Notification>()

            // Check if the result is valid
            if (result.isNotEmpty()) {
                return@withContext result
            } else {
                println("No unread notifications found for user $userId")
                return@withContext emptyList()
            }
        } catch (e: Exception) {
            println("Error fetching unread notifications: ${e.localizedMessage}")
            return@withContext emptyList()  // Returning an empty list in case of failure
        }
    }
}
suspend fun getAllNotifications(userId: Int): List<Notification> {
    return withContext(Dispatchers.IO) {
        try {
            val result = postgrest.from("user_notifications").select {
                filter {
                eq("user_id", userId)
            }
                order("created_at", Order.DESCENDING) // Sorting notifications by creation date
            }.decodeList<Notification>()
            return@withContext result
        } catch (e: Exception) {
            println("Error fetching all notifications: ${e.localizedMessage}")
            emptyList() // Return an empty list if an error occurs
        }
    }
}

suspend fun updateNotificationAsReadInDatabase(notificationId: Int): Boolean {
    return withContext(Dispatchers.IO) {
        try {
            postgrest.from("user_notifications").update(
                mapOf("is_read" to true)
            ) {
                filter {
                    eq("id", notificationId)
                }
            }
            println("Notification $notificationId marked as read.")
            true
        } catch (e: Exception) {
            println("Error marking notification as read: ${e.localizedMessage}")
            false
        }
    }
}

suspend fun markAllNotificationsAsRead(userId: Int): Boolean {
    return withContext(Dispatchers.IO) {
        try {
            postgrest.from("user_notifications").update(
                mapOf("is_read" to true)
            ) {
                filter {
                    eq("user_id", userId)
                    eq("is_read", false)
                }
            }
            println("All notifications marked as read for user $userId.")
            true
        } catch (e: Exception) {
            println("Error marking all notifications as read: ${e.localizedMessage}")
            false
        }
    }
}

suspend fun insertNotification(notification: Notification) {
    return withContext(Dispatchers.IO) {
        try {
            println("Inserting notification: $notification")

            val result = postgrest.from("user_notifications")
                .insert(notification)

            println("Insert result: $result")
        } catch (e: Exception) {
            println("General error occurred while inserting notification: ${e.localizedMessage}")
        }
    }
}

suspend fun sendFriendNotification(currentUser: Int, userToFriend: Int, action: String) {
    val currentUserInfo = getPrivateUser(currentUser)
    val friendInfo = getPrivateUser(userToFriend)

    val currentName = "${currentUserInfo?.firstName ?: "Unknown"} ${currentUserInfo?.lastName ?: "User"}"
    val friendName = "${friendInfo?.firstName ?: "Unknown"} ${friendInfo?.lastName ?: "User"}"

    val messageForCurrentUser = when (action) {
        "accepted" -> "You and $friendName are now friends."
        "declined" -> "You declined the friend request from $friendName."
        "canceled" -> "You canceled your friend request to $friendName."
        "unfriended" -> "You have removed $friendName from your friends list."
        "requested" -> "You sent a friend request to $friendName."
        else -> "Unknown friend action"
    }

    val messageForFriend = when (action) {
        "accepted" -> "You and $currentName are now friends."
        "requested" -> "$currentName has sent you a friend request."
        else -> null
    }

    insertNotification(
        Notification(
            user_id = currentUser,
            message = messageForCurrentUser,
            is_read = false,
            type = NotificationType.FRIEND_ACTION
        )
    )

    if (messageForFriend != null) {
        insertNotification(
            Notification(
                user_id = userToFriend,
                message = messageForFriend,
                is_read = false,
                type = NotificationType.FRIEND_ACTION
            )
        )
    }
}

suspend fun deleteAllNotificationsForUser(userId: Int): Boolean {
    return withContext(Dispatchers.IO) {
        try {
            postgrest.from("user_notifications")
                .delete {
                    filter {
                        eq("user_id", userId)
                    }
                }
            println("All notifications deleted for user $userId.")
            true
        } catch (e: Exception) {
            println("Error deleting all notifications: ${e.localizedMessage}")
            false
        }
    }
}

suspend fun deleteAllUnreadNotificationsForUser(userId: Int): Boolean {
    return withContext(Dispatchers.IO) {
        try {
            postgrest.from("user_notifications")
                .delete {
                    filter {
                        eq("user_id", userId)
                        eq("is_read", false) // Only delete unread notifications
                    }
                }
            true
        } catch (e: Exception) {
            println("Error deleting notifications: ${e.localizedMessage}")
            false
        }
    }
}