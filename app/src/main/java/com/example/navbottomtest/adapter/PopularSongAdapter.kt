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
import com.example.navbottomtest.models.SongModel

class PopularSongAdapter(private val popularSongs: List<SongModel>):RecyclerView.Adapter<PopularSongAdapter.MyViewHolder>() {
    inner  class MyViewHolder(private  val popularSongsView:View):RecyclerView.ViewHolder(popularSongsView){

        private val songname=itemView.findViewById<TextView>(R.id.popular_song_name)
        private val songimage=itemView.findViewById<ImageView>(R.id.popular_song_cover)
        fun bindData(trendingSong: SongModel){
            songname.text=trendingSong.song_name
            Glide.with(songimage).load(trendingSong.cover_image)
                .apply(
                    RequestOptions().transform(RoundedCorners(32))
                )
                .into(songimage)
            popularSongsView.setOnClickListener {
                Log.d("SongClick ","Clicked on Song:${trendingSong.song_name}")
                Exoplayer.startSong(it.context,trendingSong)
                it.context.startActivity(Intent(it.context, SongPlayer::class.java))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val popularsongsview=
            LayoutInflater.from(parent.context).inflate(R.layout.musiccards7,parent,false)
        return MyViewHolder(popularsongsview)
    }

    override fun getItemCount(): Int {
        return  popularSongs.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindData(popularSongs[position])
    }
}