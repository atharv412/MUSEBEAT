package com.example.navbottomtest.models

import kotlinx.serialization.Serializable
import kotlinx.datetime.*

@Serializable
data class SongModel(
    val id:Int,
    val song_name:String,
    val song_url:String,
    val artist_name:String,
    val song_movie:String,
//    @Serializable(with = DateTimeUnitSerializer::class)
    val song_releasing_date:String,
    val song_category:Int,
    val cover_image:String
){
    constructor():this(0,"","","","","",0,"")
}
