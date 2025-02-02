package com.example.navbottomtest.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer

@Serializable
data class CategoryModel(
    val id:Int,
    val name:String,
    val imageurl:String,

){
    constructor():this(0,"","")
}
