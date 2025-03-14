package com.example.navbottomtest

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.core.content.ContextCompat
import androidx.media3.common.util.UnstableApi
import com.example.navbottomtest.models.SongModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

class SongPlayer:AppCompatActivity() {

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var playbackStateBuilder: PlaybackStateCompat.Builder
    lateinit var exoPlayer: ExoPlayer
    private lateinit var mediaNotificationManager: MediaNotificationManager

    private var  playerListener= object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            playerStatus(isPlaying)
            updatePlaybackState(isPlaying)
            if (isPlaying){
                showNotification()
            }
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_song_player)
        setupMediaSession()

        mediaNotificationManager=MediaNotificationManager(this,mediaSession)

        val songname=findViewById<TextView>(R.id.song_title_text_view)
        val artistname=findViewById<TextView>(R.id.song_artist_text_view)
        val songImage=findViewById<ImageView>(R.id.song_image_view)
        val playerview=findViewById<PlayerView>(R.id.player_view)
        val back=findViewById<ImageButton>(R.id.backButton)
        val download=findViewById<ImageView>(R.id.download_button)


        Exoplayer.getCurrentSong()?.apply {

            songname.text=song_name
            artistname.text=artist_name

            if (cover_image.isEmpty()){
                songImage.setBackgroundColor(Color.WHITE)
                songImage.setImageResource(R.drawable.round_music_note_24)
            }else{
                Glide.with(songImage).load(cover_image)
                    .apply(
                        RequestOptions().transform(RoundedCorners(32))
                    )
                    .into(songImage)
            }
//
            exoPlayer= Exoplayer.getInstance(this@SongPlayer)!!
            exoPlayer.addListener(playerListener)
            playerview.player=exoPlayer
            playerview.showController()
            showNotification()
        }
//        val bgColor=MaterialColors.getColor(this,com.google.android.material.R.attr.colorSurface)
//        val playerView: PlayerView = findViewById(R.id.player_view)
//        playerView.setBackgroundColor(Color.TRANSPARENT)

        back.setOnClickListener{
            finish()
        }

        download.setOnClickListener {
            val currentsong=Exoplayer.getCurrentSong()!!

            downloadFileFromSupabase(this@SongPlayer,currentsong.song_url,currentsong.song_name)

        }

    }

    fun saveFileToDownloads(context: Context, fileName: String, inputStream: InputStream) {

            val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "audio/mpeg") // Change based on file type
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let {
                val outputStream: OutputStream? = resolver.openOutputStream(it)
                outputStream?.use { stream ->
                    inputStream.copyTo(stream)
                }
                println("File saved to Downloads: $fileName")
            }
        }


    private fun downloadFileFromSupabase(context: Context, fileUrl: String, fileName: String) {
        CoroutineScope(Dispatchers.IO).launch {

            try {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Download started...", Toast.LENGTH_SHORT).show()
                }
                val url = URL(fileUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.doInput = true
                connection.connect()

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = connection.inputStream
                    saveFileToDownloads(context, fileName, inputStream)
                    inputStream.close()
                    withContext(Dispatchers.Main){
                        Toast.makeText(context,"Song downloaded to the downloads folder",Toast.LENGTH_LONG).show()

                    }
                } else {
                    println("Failed to download  HTTP Code: ${connection.responseCode}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    private fun setupMediaSession(){
        mediaSession=MediaSessionCompat(this,"MyMediaSession")
        playbackStateBuilder=PlaybackStateCompat.Builder()
            .setActions(
                PlaybackStateCompat.ACTION_PLAY or
                PlaybackStateCompat.ACTION_PAUSE
            )
        mediaSession.setPlaybackState(playbackStateBuilder.build())
        mediaSession.isActive=true


        mediaSession.setCallback(object :MediaSessionCompat.Callback(){
            override fun onPlay() {
                exoPlayer.play()
            }

            override fun onPause() {
                exoPlayer.pause()
            }
        })
    }

    private fun showNotification() {
        Exoplayer.getCurrentSong()?.apply {
            mediaNotificationManager.createNotification(song_name, artist_name, cover_image)
        }
    }

    private  fun updatePlaybackState(isPlaying:Boolean){
        val state = if (isPlaying)
            PlaybackStateCompat.STATE_PLAYING
        else
            PlaybackStateCompat.STATE_PAUSED

        playbackStateBuilder.setState(state, exoPlayer.currentPosition, 1f)
        mediaSession.setPlaybackState(playbackStateBuilder.build())
    }

    override fun onDestroy(){
        super.onDestroy()
        exoPlayer?.removeListener(playerListener)
    }

    fun playerStatus(show:Boolean){
            val playingstatus=findViewById<ImageView>(R.id.song_gif_image_view)
            val playing=findViewById<TextView>(R.id.nowPlaying)
        if (show){
            playingstatus.visibility=View.VISIBLE
            playing.visibility=View.VISIBLE
        }
        else{
            playingstatus.visibility=View.GONE
//            Toast.makeText(this,"Error playing Song try any other song",Toast.LENGTH_SHORT).show()
        }
    }
}