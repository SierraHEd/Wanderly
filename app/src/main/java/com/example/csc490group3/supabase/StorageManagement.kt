package com.example.csc490group3.supabase

import com.example.csc490group3.supabase.SupabaseManagement.supabase
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*

object StorageManagement {

    private const val BUCKET_NAME = "media"

    private val storage: Storage get() = supabase.storage


    suspend fun uploadPhoto(file: File, userId: String): String? {
        return uploadFile(file, userId, "photos")
    }

    suspend fun uploadEventPhoto(file: File, eventId: String): String? {
        return uploadFile(file, eventId, "event_photos")
    }


    suspend fun uploadVideo(file: File, userId: String): String? {
        return uploadFile(file, userId, "videos")
    }


    private suspend fun uploadFile(file: File, userId: String, mediaType: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val fileName = "${userId}_${UUID.randomUUID()}_${file.name}"
                val path = "$mediaType/$fileName"

                storage[BUCKET_NAME].upload(path, file.readBytes()) {
                    upsert = true
                }

                // Generate URL for public access
                val publicUrl = storage[BUCKET_NAME].publicUrl(path)

                println("File uploaded successfully: $publicUrl")
                publicUrl
            } catch (e: Exception) {
                println("Error uploading file: ${e.localizedMessage}")
                null
            }
        }
    }

}