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
import com.example.navbottomtest.models.SongModel
import com.example.navbottomtest.R
import com.example.navbottomtest.SongPlayer


class UserSearchHistoryAdapter(private val playedSongs:List<SongModel>):RecyclerView.Adapter<UserSearchHistoryAdapter.MyViewHolder>() {
    class  MyViewHolder(private val playedSongsView: View):
        RecyclerView.ViewHolder(playedSongsView){
        private val playedSongName=playedSongsView.findViewById<TextView>(R.id.popularMixTitle)
        private val playedSongsCoverImage=playedSongsView.findViewById<ImageView>(R.id.popularMixImage)

        fun bindData(playedSong:SongModel){
            playedSongName.text=playedSong.song_name
            Glide.with(playedSongsCoverImage).load(playedSong.cover_image)
                .apply(
                    RequestOptions().transform(RoundedCorners(32))
                ).into(playedSongsCoverImage)

            playedSongsView.setOnClickListener {
                Log.d("SongClick ","Clicked on Song:${playedSong.song_name}")
                Exoplayer.startSong(it.context,playedSong)
                it.context.startActivity(Intent(it.context, SongPlayer::class.java))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val searchrecentlyplayed=LayoutInflater.from(parent.context).inflate(R.layout.popular_mixes,parent,false)
        return MyViewHolder(searchrecentlyplayed)
//                val searchrecentlyplayed= LayoutInflater.from(parent.context).inflate(R.layout.popular_mixes,parent,false)
    }

    override fun getItemCount(): Int {
        return playedSongs.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindData(playedSongs[position])
    }
}