package com.example.navbottomtest

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.graphics.Color
import android.media.AudioManager
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
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.navbottomtest.adapter.AddToPlaylistAdapter
import com.example.navbottomtest.adapter.PlaylistAdapter
import com.example.navbottomtest.models.PlaylistModel
import com.example.navbottomtest.models.SongModel
import com.example.navbottomtest.models.UserModel
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
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
    private val supaClient = SupabaseClientProvider.client
//    private lateinit var playlistAdapter: PlaylistAdapter
    private lateinit var addToPlaylistAdapter: AddToPlaylistAdapter



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
        val volume=findViewById<ImageView>(R.id.volumeCntrl)
        val addToPlaylist=findViewById<ImageView>(R.id.add_to_playlist)

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

        volume.setOnClickListener {
            val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            audioManager.adjustStreamVolume(
                AudioManager.STREAM_MUSIC,
                AudioManager.ADJUST_SAME,
                AudioManager.FLAG_SHOW_UI
            )
        }
        download.setOnClickListener {
            val currentsong=Exoplayer.getCurrentSong()!!

            downloadFileFromSupabase(this@SongPlayer,currentsong.song_url,currentsong.song_name)

        }
        addToPlaylist.setOnClickListener {
            showAddToPlaylistDialog()
        }

    }

    @OptIn(UnstableApi::class)
    private  fun showAddToPlaylistDialog(){
        val dialogView = LayoutInflater.from(this).inflate(R.layout.playlist_creator, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()
        val btnCreate = dialogView.findViewById<Button>(R.id.btnCreatePlaylist)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)
        val playlistTag=dialogView.findViewById<TextView>(R.id.txtSelectSongs)
        val playlistSearchInput=dialogView.findViewById<EditText>(R.id.search_song_for_playlist)
        val editTextPlaylistName = dialogView.findViewById<EditText>(R.id.editPlaylistName)
        editTextPlaylistName.visibility=View.GONE
        playlistTag.text = "Select Playlist to add the song to "
        getPlaylists(dialogView)
//        playlistSearchInput.addTextChangedListener(object :TextWatcher{
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                println(p0)
//            }
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                TODO("Not yet implemented")
//            }
//
//            override fun afterTextChanged(p0: Editable?) {
//                TODO("Not yet implemented")
//            }
//        })
        btnCreate.setOnClickListener {
            // TODO: add the song to the selected playlist
            CoroutineScope(Dispatchers.IO).launch {
                val selectedPlaylists=addToPlaylistAdapter.getSelectedPlaylists()
                println("PLAYLIST WILL BE ADDED TO THESE PLAYLISTS "+selectedPlaylists)
                val currentUser = supaClient.auth.currentSessionOrNull()?.user?.email.toString()
                val response = supaClient.from("user")
                    .select {
                        filter {
                            eq("user_email", currentUser)
                        }
                    }
                    .decodeSingle<UserModel>()
                val id=response.id
                val songId= Exoplayer.getCurrentSong()?.id
                val columns= selectedPlaylists.map {name->
                    PlaylistModel(
                        playlist_name = name,
                        song_id = songId!!,
                        user_id = id)
                }
                println(" values to be inserted in playlist are as follows: $columns")

                Log.d("atharva","values to be inserted are as follows $columns")

                val createdPLaylist=supaClient
                    .from("user_playlist")
                    .insert(columns)

                if (createdPLaylist.data.isNotEmpty()) {
                    Log.d("atharva", "playlist created ")
                }
            }
            dialog.dismiss()
        }
        btnCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
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

    private  fun getPlaylists(rootView: View){
        CoroutineScope(Dispatchers.IO).launch {
            val currentUser = supaClient.auth.currentSessionOrNull()?.user?.email.toString()

            val response = supaClient.from("user")
                .select {
                    filter {
                        eq("user_email", currentUser)
                    }

                }
                .decodeSingle<UserModel>()

            val playlists = supaClient.from("user_playlist")
                .select {
                    filter {
                        eq("user_id", response.id.toString())
                    }
                }
                .decodeList<PlaylistModel>()

            //METHOD 1
            val groupedPlaylists = playlists
                .groupBy { it.playlist_name }
                .mapValues { it.value.map { song -> song.song_id } }

            //METHOD 2
//            val groupedPlaylists=playlists
//                .groupBy { it.playlist_name }
//            val simplifiedPlaylists = groupedPlaylists
//                .mapValues { (_, songs) ->
//                    songs.map { it.song_id }
//                }
            println("the grouped  list is $groupedPlaylists")
            withContext(Dispatchers.Main){
                setupPlaylistList(rootView,groupedPlaylists)
            }

        }
    }
    private fun setupPlaylistList(rootView: View, playlistNameList:Map<String,List<Int>>){
        val recyclerView=rootView.findViewById<RecyclerView>(R.id.recyclerViewSongs)
        addToPlaylistAdapter=AddToPlaylistAdapter(playlistNameList)
        recyclerView.layoutManager=LinearLayoutManager(rootView.context,LinearLayoutManager.VERTICAL,false)
        recyclerView.adapter=addToPlaylistAdapter
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