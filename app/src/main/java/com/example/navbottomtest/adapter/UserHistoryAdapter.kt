package com.example.navbottomtest.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.navbottomtest.Exoplayer
import com.example.navbottomtest.R
import com.example.navbottomtest.SongPlayer
import com.example.navbottomtest.SupabaseClientProvider
import com.example.navbottomtest.models.SongModel

class UserHistoryAdapter(private val playedSongs: List<SongModel?>):
RecyclerView.Adapter<UserHistoryAdapter.MyViewHolder>(){
    class  MyViewHolder(private  val playedSongsView:View):
            RecyclerView.ViewHolder(playedSongsView){

                private val playedSongName:TextView=itemView.findViewById(R.id.musicTitle)
                private val playedSongsCoverImage:ImageView=itemView.findViewById(R.id.cover_image_view)
                private val playedSongsArtistName:TextView=itemView.findViewById(R.id.name_text_view)
                private val client= SupabaseClientProvider.client


                fun bindData(playedSong:SongModel){
                    playedSongName.text=playedSong.song_name
                    playedSongsArtistName.text=playedSong.artist_name
                    Glide.with(playedSongsCoverImage).load(playedSong.cover_image)
                        .apply(
                            RequestOptions().transform(RoundedCorners(32))
                        ).into(playedSongsCoverImage)

                    playedSongsView.setOnClickListener {
                        Log.d("SongClick ","Clicked on Song:${playedSong.song_name}")
                        Exoplayer.startSong(it.context,playedSong)
                        it.context.startActivity(Intent(it.context,SongPlayer::class.java))
                    }
                }
            }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val recentlyPlayedView=LayoutInflater.from(parent.context).inflate(R.layout.homemusic_item,parent,false)
        return  MyViewHolder(recentlyPlayedView)
    }

    override fun getItemCount(): Int {
        return playedSongs.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        playedSongs[position]?.let { holder.bindData(it) }
    }
}