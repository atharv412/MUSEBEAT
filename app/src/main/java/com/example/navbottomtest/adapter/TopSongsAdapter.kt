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

class TopSongsAdapter(private  val topSongs:List<SongModel>):
    RecyclerView.Adapter<TopSongsAdapter.MyViewHolder>()
{
    class  MyViewHolder(private  val topSongsView:View):
        RecyclerView.ViewHolder(topSongsView) {

        private val songName: TextView = itemView.findViewById(R.id.name_text_view)
        private  val songCoverImage: ImageView =itemView.findViewById(R.id.cover_image_view)

        fun bindData(topSong: SongModel){
            songName.text=topSong.song_name
            Glide.with(songCoverImage).load(topSong.cover_image)
                .apply(
                    RequestOptions().transform(RoundedCorners(32))
                )
                .into(songCoverImage)

            topSongsView.setOnClickListener {
                Log.d("SongClick ","Clicked on Song:${topSong.song_name}")
                Exoplayer.startSong(it.context,topSong)
                it.context.startActivity(Intent(it.context, SongPlayer::class.java))
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val topsongsview=LayoutInflater.from(parent.context).inflate(R.layout.category_recycler_row_item,parent,false)
        return  MyViewHolder(topsongsview)
    }

    override fun getItemCount(): Int {
        return  topSongs.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindData(topSongs[position])
    }
}