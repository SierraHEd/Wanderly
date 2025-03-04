package com.example.csc490group3.supabase

import com.example.csc490group3.supabase.SupabaseManagement.AuthManagement.auth
import io.github.jan.supabase.auth.providers.builtin.Email

object AuthManagement {

    suspend fun accountValidation(userEmail: String, userPassword: String): Boolean {
        try {
            val response = auth.signInWith(Email) {
                email = userEmail
                password = userPassword
            }
            return true
        }catch(e: Exception) {
            return false
        }
    }
    suspend fun getActiveUser() {
        val user = auth.retrieveUserForCurrentSession(updateSession = true)
    }
}