package com.example.navbottomtest.models
import kotlinx.serialization.Serializable


@Serializable
data class PlaylistModel(
    val id:Int?=null,
    val playlist_name:String,
    val song_id:Int,
    val user_id: Int?,
)
{
    constructor():this(0,"",0,0, )
}