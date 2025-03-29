package com.example.navbottomtest.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.navbottomtest.R


class AddToPlaylistAdapter (
    private  val playlistList:Map<String,List<Int>>,
    private val selectedPlaylists:MutableList<String> = mutableListOf()

):RecyclerView.Adapter<AddToPlaylistAdapter.MyViewHolder>(){
    inner class MyViewHolder(private val playlistView:View):
        RecyclerView.ViewHolder(playlistView){
            private val playlistName=playlistView.findViewById<TextView>(R.id.songTitle)
            private val songArtist=playlistView.findViewById<TextView>(R.id.artistName)
            private val songCheckBox=playlistView.findViewById<CheckBox>(R.id.checkboxSelectSong)
            private val playlistCheckBox=playlistView.findViewById<CheckBox>(R.id.checkboxSelectSong)

            fun bindData(playlist:String){
                playlistName.text=playlist
                songArtist.visibility=View.GONE

                playlistCheckBox.setOnCheckedChangeListener(null)
                songCheckBox.isChecked=selectedPlaylists.contains(playlist)

                songCheckBox.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked){
                        selectedPlaylists.add(playlist)
                    }else{
                        selectedPlaylists.remove(playlist)
                    }
                }
            }
        }

    fun getSelectedPlaylists():List<String> = selectedPlaylists.toList()
//TODO ADD THE FUNCTIONALITY TO SELECT PLAYLIST NAMES TO ADD THE SONG BASED ON THE SELECTED PLAYLIST and return the playlist name back to onClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val playlistview2=LayoutInflater.from(parent.context).inflate(R.layout.playlist_song_selector,parent,false)
        return  MyViewHolder(playlistview2)
    }

    override fun getItemCount(): Int {
        return  playlistList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val playlistname=playlistList.keys.toList()[position]
        holder.bindData(playlistname)
    }
}