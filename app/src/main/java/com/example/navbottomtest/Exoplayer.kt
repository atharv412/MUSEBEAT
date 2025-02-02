package com.example.navbottomtest

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.navbottomtest.models.SongModel

object Exoplayer {
    private  var exoPlayer: ExoPlayer?=null
    private  var currentSong:SongModel?=null

    fun getCurrentSong():SongModel?{
        return currentSong
    }

    fun getInstance():ExoPlayer?{
        return exoPlayer
    }

    fun startSong(context: Context,song:SongModel){
        if (exoPlayer==null){ exoPlayer=ExoPlayer.Builder(context).build()}

        if (currentSong!=song){
            currentSong=song

            currentSong?.song_url?.apply {
                val mediaItem=MediaItem.fromUri(this)
                exoPlayer?.setMediaItem(mediaItem)
                exoPlayer?.prepare()
                exoPlayer?.play()
            }
        }
    }
}