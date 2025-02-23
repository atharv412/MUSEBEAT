package com.example.navbottomtest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.bumptech.glide.Glide

class MiniPlayer : Fragment() {
    private lateinit var miniPlayerTitle: TextView
    private lateinit var miniPlayerArtist: TextView
    private lateinit var miniPlayerArtwork: ImageView
    private lateinit var playPauseButton: ImageView
    private var player: ExoPlayer? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.mini_player, container, false)

        miniPlayerTitle = view.findViewById(R.id.miniPlayerTitle)
        miniPlayerArtist = view.findViewById(R.id.miniPlayerArtist)
        miniPlayerArtwork = view.findViewById(R.id.miniPlayerArtwork)
        playPauseButton = view.findViewById(R.id.miniPlayerPlayPause)


        view.setOnClickListener {
            val i=Intent(context,SongPlayer::class.java)
            startActivity(i)
        }
        // Set Media Item (Replace URL with a real one)

         player = Exoplayer.getInstance(requireContext())
        if (player == null) {
            Log.e("MiniPlayer", "ExoPlayer instance is NULL!")
        }

        updateUI()
        setupPlayerListener()


        // Play/Pause Toggle
//        playPauseButton.setOnClickListener {
//
//            var status=player!!.isPlaying
//            if (status){
////                player!!.pause()
//                    playPauseButton.setImageResource(R.drawable.round_play_arrow_white_24)
//
//            }
//            else{
//                player!!.play()
//                    playPauseButton.setImageResource(R.drawable.round_pause_24)
//
//            }
//        }
        playPauseButton.setOnClickListener {
            player?.let {
                println(it.isPlaying)
                if (it.isPlaying) {
                    it.pause()  // ✅ Properly pause the player
                } else {
                    it.play()   // ✅ Properly start playback
                }
            }
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    private fun updateUI() {
        val currentSong = Exoplayer.getCurrentSong()

        if (currentSong != null) {
            miniPlayerTitle.text = currentSong.song_name
            miniPlayerArtist.text = currentSong.artist_name

            // Load artwork (Use Glide or Picasso)
            Glide.with(miniPlayerArtwork)
                .load(currentSong.cover_image) // Assuming song_cover is a URL
                .placeholder(R.drawable.round_music_note_white_24)
                .into(miniPlayerArtwork)

            // Update play/pause button
            playPauseButton.setImageResource(
                if (player?.isPlaying == true) R.drawable.round_pause_24
                else R.drawable.round_play_arrow_white_24
            )
//            if (player?.isPlaying == true) {
//
//                playPauseButton.setImageResource(R.drawable.round_pause_24)
//            } else {
//                playPauseButton.setImageResource(R.drawable.round_play_arrow_white_24)
//            }
        }
    }

    private fun setupPlayerListener() {
//        player?.removeListener(playerListener)
        player?.addListener(playerListener)
    }

    private val playerListener = object : Player.Listener {
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            activity?.runOnUiThread { updateUI() }  // Refresh UI when track changes
        }

        override fun onPlaybackStateChanged(state: Int) {
            activity?.runOnUiThread { updateUI() }  // Ensure UI updates when ExoPlayer state changes
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            activity?.runOnUiThread {
                playPauseButton.setImageResource(
                    if (isPlaying) R.drawable.round_pause_24
                    else R.drawable.round_play_arrow_white_24
                )
            }
//            updateUI() // Called when play/pause state changes
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        player?.removeListener(playerListener)
    }
}
