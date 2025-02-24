package com.example.csc490group3.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("individual")
data class privateUser (
    override val id: Int? = null,
    override val email: String,
    @SerialName("first_name")
    val firstName: String,
    @SerialName("last_name")
    val lastName: String,
    val birthday: LocalDate,
    val public: Boolean,
    val affiliation: String?,
    val likedCategories: Set<category> = emptySet()
) : user()


