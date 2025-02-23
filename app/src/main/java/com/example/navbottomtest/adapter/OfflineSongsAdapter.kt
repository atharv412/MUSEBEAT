package com.example.navbottomtest.adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.navbottomtest.Exoplayer
import com.example.navbottomtest.models.OfflineSongsModel
import com.example.navbottomtest.R

import com.example.navbottomtest.models.SongModel

class OfflineSongsAdapter (private  val offlineSongs:List<OfflineSongsModel>):
    RecyclerView.Adapter<OfflineSongsAdapter.MyViewHolder>(){

    class  MyViewHolder(private val offlineSongsView:View):
            RecyclerView.ViewHolder(offlineSongsView){

            private val songName: TextView = itemView.findViewById(R.id.name_text_view)
            private  val songCoverImage: ImageView =itemView.findViewById(R.id.cover_image_view)

        fun bindData(offlineSong: OfflineSongsModel){
            songName.text=offlineSong.title
            songCoverImage.setBackgroundColor(Color.WHITE)
            songCoverImage.setImageResource(R.drawable.round_music_note_24)

            offlineSongsView.setOnClickListener {
                Log.d("Offline SongClick","Clicked on song${offlineSong.title} with path${offlineSong.path}")
                val offSong = SongModel(
                    0,
                    offlineSong.title,
                    offlineSong.path,
                    "",
                    "",
                    "",
                    0,
                    "")
//                offSong.song_url=offlineSong.path
                Exoplayer.startSong(it.context,offSong)
            }
//            Glide.with()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val offlinesongsview=LayoutInflater.from(parent.context).inflate(R.layout.category_recycler_row_item,parent,false)
        return  MyViewHolder(offlinesongsview)
    }

    override fun getItemCount(): Int {
        return offlineSongs.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindData(offlineSongs[position])
    }
}