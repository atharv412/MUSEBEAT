package com.example.navbottomtest.models
import kotlinx.serialization.Serializable


@Serializable
data class OfflineSongsModel(
    val title:String,
    val path:String
) {
    constructor():this("","")
}