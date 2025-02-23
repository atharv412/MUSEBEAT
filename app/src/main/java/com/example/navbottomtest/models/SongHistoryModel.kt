package com.example.navbottomtest.models
import kotlinx.serialization.Serializable


@Serializable
data class SongHistoryModel(
    val id:Int?=null,
    val user_id: Int?,
    val song_id:Int,
) {
    constructor():this(0,0,0, )
}