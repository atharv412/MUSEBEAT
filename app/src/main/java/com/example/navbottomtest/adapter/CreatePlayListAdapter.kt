package com.example.navbottomtest.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.navbottomtest.models.SongModel
import com.example.navbottomtest.R


class CreatePlayListAdapter(
    private val songList:List<SongModel>,
    private val selectedSongs: MutableList<SongModel> = mutableListOf()

):
    RecyclerView.Adapter<CreatePlayListAdapter.MyViewHolder>(){

    inner class MyViewHolder(private val songSelectionView:View)
        :RecyclerView.ViewHolder(songSelectionView)
    {
        private val songTitle=songSelectionView.findViewById<TextView>(R.id.songTitle)
        private val songArtist=songSelectionView.findViewById<TextView>(R.id.artistName)
        private val songImage=songSelectionView.findViewById<ImageView>(R.id.imgSongThumbnail)
        private val songCheckBox=songSelectionView.findViewById<CheckBox>(R.id.checkboxSelectSong)

        fun bindData(song:SongModel){
            songTitle.text=song.song_name
            songArtist.text=song.artist_name
            Glide.with(songImage).load(song.cover_image)
                .apply(
                    RequestOptions().circleCrop()
                )
                .into(songImage)

            songCheckBox.setOnCheckedChangeListener(null)

            songCheckBox.isChecked=selectedSongs.contains(song)

            songCheckBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedSongs.add(song)
                } else {
                    selectedSongs.remove(song)
                }
            }
        }
    }

    fun getSelectedSongs(): List<SongModel> = selectedSongs.toList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val songselectionView=LayoutInflater.from(parent.context).inflate(R.layout.playlist_song_selector,parent,false)
        return MyViewHolder(songselectionView)
    }

    override fun getItemCount(): Int {
        return songList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
       holder.bindData(songList[position])
    }

}