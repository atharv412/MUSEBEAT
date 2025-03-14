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

class SearchAdapter (private  val songList: List<SongModel>):
    RecyclerView.Adapter<SearchAdapter.MyViewHolder>()
    {
        class MyViewHolder(private  val searchView: View):
            RecyclerView.ViewHolder(searchView)
        {
            private  val songImage:ImageView=searchView.findViewById(R.id.songThumbnail)
            private  val songTitle:TextView=searchView.findViewById(R.id.songTitle)
            private  val songPlay:ImageView=searchView.findViewById(R.id.playButton)
            private  val songArtist:TextView=searchView.findViewById(R.id.songDuration)
            fun bindData(searchSong: SongModel){
                songTitle.text=searchSong.song_name
                Glide.with(songImage).load(searchSong.cover_image)
                    .apply (
                        RequestOptions().transform(RoundedCorners(32))
                    )
                    .into(songImage)
                songArtist.text=searchSong.artist_name

                songPlay.setOnClickListener{
                    Log.d("SongClick", "Clicked on song: ${searchSong.song_name}")
                    Exoplayer.startSong(it.context,searchSong)

                }

                searchView.setOnClickListener {
                    Log.d("SongClick", "Clicked on song: ${searchSong.song_name}")
                    Exoplayer.startSong(it.context,searchSong)
                    it.context.startActivity(Intent(it.context,SongPlayer::class.java))

                }
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int ): MyViewHolder {
            val searchView=LayoutInflater.from(parent.context).inflate(R.layout.search_song_recycler_row_2,parent,false)
            return MyViewHolder(searchView)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.bindData(songList[position])
        }

        override fun getItemCount(): Int {
            return  songList.size
        }
//TODO TEST THIS SETUP//successful vitals lookin good
    }