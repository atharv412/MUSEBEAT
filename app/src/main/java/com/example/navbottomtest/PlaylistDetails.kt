package com.example.navbottomtest

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.navbottomtest.adapter.SongListAdapter
import com.example.navbottomtest.models.SongModel
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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
        setContentView(R.layout.playllist3)
        println("this is inner side of playlists and songs are as follows $playlistdetails")

        val playlistName=findViewById<TextView>(R.id.playlist_title)
        val back=findViewById<ImageButton>(R.id.backButton)
//        val play=findViewById<ImageView>(R.id.play_button)
        val userName=findViewById<TextView>(R.id.playlist_user)

        playlistName.text= playlistdetails.keys.firstOrNull()
        val currentUser = supaClient.auth.currentSessionOrNull()?.user?.email.toString()
        userName.text="curated by ${currentUser}"

        getSongsBasedOnPlaylistName{
            songs->

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
                val songUrls: List<String> = songs.map { it.cover_image }
                val playlistCover=findViewById<ImageView>(R.id.playlist_image)
                createPlaylistCollage(this@PlaylistDetails,songUrls,playlistCover)
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



    fun createPlaylistCollage(
        context: Context,
        imageUrls: List<String>,
        imageView: ImageView
    ) {
        if (imageUrls.size < 4) {
            // If less than 4 images, just show the first one
            Glide.with(context).load(imageUrls.firstOrNull()).into(imageView)
            return
        }

        val imageBitmaps = mutableListOf<Bitmap>()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val glideLoad = { url: String ->
                    Glide.with(context)
                        .asBitmap()
                        .load(url)
                        .submit()
                        .get()
                }

                // Load 4 images concurrently
                val jobs = imageUrls.take(4).map { url ->
                    async { glideLoad(url) }
                }

                // Collect images
                imageBitmaps.addAll(jobs.awaitAll())

                // Create the collage
                val collageBitmap = mergeBitmaps(imageBitmaps)

                // Set it to the ImageView on the main thread
                withContext(Dispatchers.Main) {
                    imageView.setImageBitmap(collageBitmap)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun mergeBitmaps(bitmaps: List<Bitmap>): Bitmap {
        val collageSize = 300 // Final collage size (adjust as needed)
        val result = Bitmap.createBitmap(collageSize, collageSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)

        val paint = Paint().apply {
            isAntiAlias = true
            isFilterBitmap = true
        }

        val halfSize = collageSize / 2
        val rects = listOf(
            Rect(0, 0, halfSize, halfSize),               // Top-left
            Rect(halfSize, 0, collageSize, halfSize),      // Top-right
            Rect(0, halfSize, halfSize, collageSize),      // Bottom-left
            Rect(halfSize, halfSize, collageSize, collageSize) // Bottom-right
        )

        for (i in bitmaps.indices) {
            val resizedBitmap = Bitmap.createScaledBitmap(bitmaps[i], halfSize, halfSize, false)
            canvas.drawBitmap(resizedBitmap, null, rects[i], paint)
        }

        return result
    }

}
