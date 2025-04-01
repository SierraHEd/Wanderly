package com.example.csc490group3.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: Int,
    val name: String
)

/*
{

    MUSIC(1, "Music"),
    BUSINESS_AND_PROFESSIONAL(2, "Business & Professional"),
    FOOD_AND_DRINK(3, "Food & Drink"),
    COMMUNITY_AND_CULTURE(4, "Community & Culture"),
    PERFORMING_AND_VISUAL_ARTS(5, "Performing & Visual Arts"),
    FILM_MEDIA_AND_ENTERTAINMENT(6, "Film, Media & Entertainment"),
    SPORTS_AND_FITNESS(7, "Sports & Fitness"),
    HEALTH_AND_WELLNESS(8, "Health and Wellness"),
    SCIENCE_AND_TECHNOLOGY(9, "Science & Technology"),
    TRAVEL_AND_OUTDOOR(10, "Travel & Outdoor"),
    CHARITY_AND_CAUSES(11, "Charity & Causes"),
    RELIGION_AND_SPIRITUALITY(12, "Religion & Spirituality"),
    FAMILY_AND_EDUCATION(13, "Family & Education"),
    SEASONAL_AND_HOLIDAY(14, "Seasonal & Holiday"),
    GOVERNMENT_AND_POLITICS(15, "Government & Politics"),
    FASHION_AND_BEAUTY(16, "Fashion & Beauty"),
    HOME_AND_LIFESTYLE(17, "Home & Lifestyle"),
    AUTO_BOAT_AND_AIR(18, "Auto, Boat & Air"),
    HOBBIES_AND_SPECIAL_INTEREST(19, "Hobbies & Special Interest"),
    SCHOOL_ACTIVITIES(20, "School Activities"),
    OTHER(21, "Other")

}*/