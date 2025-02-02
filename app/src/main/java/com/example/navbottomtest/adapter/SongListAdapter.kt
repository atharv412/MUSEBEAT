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
import com.example.navbottomtest.SongPlayer
import com.example.navbottomtest.R
import com.example.navbottomtest.models.SongModel

class SongListAdapter(private  val songList:List<SongModel>):
    RecyclerView.Adapter<SongListAdapter.MyViewHolder>()
    {
        class MyViewHolder(private  val songView:View):
            RecyclerView.ViewHolder(songView){
            private  val songImageView:ImageView=songView.findViewById(R.id.song_cover_image_view)
            private  val songTitleView:TextView=songView.findViewById(R.id.song_title_text_view)
            private  val rootView:View=songView.rootView
            //
                fun bindData(song:SongModel){
                    songTitleView.text=song.song_name
//                    Glide.with(songImageView).load()
                    Glide.with(songImageView).load(song.cover_image)
                        .apply(
                            RequestOptions().transform(RoundedCorners(32))
                        )
                        .into(songImageView)

                    songView.setOnClickListener {
                        Log.d("SongClick", "Clicked on song: ${song.song_name}")
                        Exoplayer.startSong(it.context,song)
                        it.context.startActivity(Intent(it.context,SongPlayer::class.java))
                    }
                }
            }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val songView=LayoutInflater.from(parent.context).inflate(R.layout.songlist_recycler_row,parent,false)
        return MyViewHolder(songView)
    }

    override fun getItemCount(): Int {
        return songList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindData(songList[position])
    }
}