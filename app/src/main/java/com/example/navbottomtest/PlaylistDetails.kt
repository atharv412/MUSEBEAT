package com.example.navbottomtest

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class PlaylistDetails:AppCompatActivity() {

    companion object{
        lateinit var playlistdetails:Map<String,List<Int>>
    }
    private val supaClient=SupabaseClientProvider.client


    override fun onCreate(savedInstanceState: Bundle? ) {
        super.onCreate(savedInstanceState )
        enableEdgeToEdge()
        setContentView(R.layout.playlist_details_activity)
        println("this is inner side of playlists and songs are as follows $playlistdetails")
    }
}