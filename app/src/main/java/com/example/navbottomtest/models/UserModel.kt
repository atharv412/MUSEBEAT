package com.example.navbottomtest.models
import kotlinx.serialization.Serializable

@Serializable
data class UserModel (
    val id:Int?=null,
    val user_firstname:String,
    val user_email:String,
    val user_lastname:String,
    val user_profile_photo:String,
    val user_country:String
    )