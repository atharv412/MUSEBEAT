package com.example.navbottomtest

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.navbottomtest.adapter.SongListAdapter
import com.example.navbottomtest.models.SongModel
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlaylistDetails:AppCompatActivity() {

    companion object{
        lateinit var playlistdetails:Map<String,List<Int>>
    }
    private val supaClient=SupabaseClientProvider.client
    private  lateinit var songListAdapter:SongListAdapter


    override fun onCreate(savedInstanceState: Bundle? ) {
        super.onCreate(savedInstanceState )
        enableEdgeToEdge()
        setContentView(R.layout.playlist_details_activity)
        println("this is inner side of playlists and songs are as follows $playlistdetails")

        val playlistName=findViewById<TextView>(R.id.playlist_title)
        val back=findViewById<ImageButton>(R.id.backButton)
        val play=findViewById<ImageView>(R.id.play_button)
        playlistName.text= playlistdetails.keys.firstOrNull()

        getSongsBasedOnPlaylistName{
//            songs->
//            play.setOnClickListener {
//                Exoplayer.startPlaylist(this@PlaylistDetails,songs)
//            }
        }
        back.setOnClickListener{
            finish()
        }
    }
    fun getSongsBasedOnPlaylistName(callback: (List<SongModel>)->Unit){
        val songIds= playlistdetails.values.flatten()
        CoroutineScope(Dispatchers.IO).launch {
            val songs=songIds.map { songId ->
                supaClient.from("songs")
                    .select {
                        filter {
                            eq("id", songId)
                        }
                    }
                    .decodeSingle<SongModel>()
            }
                println("somgs fetched based on playlist name $songs")
            withContext(Dispatchers.Main){
                setupPlaylistSongRecyclerView(songs)
                callback(songs)
//                Exoplayer.startPlaylist(this@PlaylistDetails,songs)
            }
        }
    }
    fun  setupPlaylistSongRecyclerView(playlistSongs:List<SongModel>){
        val rowRecyclerView=findViewById<RecyclerView>(R.id.song_list)
        songListAdapter= SongListAdapter(playlistSongs)//define the songlist adapter
        rowRecyclerView.layoutManager= LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        rowRecyclerView.adapter=songListAdapter
    }
}
