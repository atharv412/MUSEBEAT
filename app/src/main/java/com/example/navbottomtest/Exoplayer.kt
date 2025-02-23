package com.example.navbottomtest

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.navbottomtest.models.SongModel
import java.io.File

object Exoplayer {
    private  var exoPlayer: ExoPlayer?=null
    private  var currentSong:SongModel?=null

    fun getCurrentSong():SongModel?{
        return currentSong
    }

    fun getInstance(context: Context):ExoPlayer?{
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(context).build()
        }
        return exoPlayer!!
    }

    fun startSong(context: Context,song:SongModel){
        if (exoPlayer==null){ exoPlayer=ExoPlayer.Builder(context).build()}

        if (currentSong!=song){
            currentSong=song

            currentSong?.song_url?.apply {

                val mediaItem=if(song.song_url.startsWith("http")||song.song_url.startsWith("https")){
                    MediaItem.fromUri(Uri.parse(song.song_url))
                }
                else{
                    MediaItem.fromUri(Uri.fromFile(File(song.song_url)))
                }
                exoPlayer?.setMediaItem(mediaItem)
                exoPlayer?.prepare()
                exoPlayer?.play()
            }
        }
    }
}