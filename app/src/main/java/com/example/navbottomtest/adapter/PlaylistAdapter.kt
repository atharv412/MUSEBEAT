package com.example.navbottomtest.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.navbottomtest.PlaylistDetails
import com.example.navbottomtest.PlaylistDetails.Companion.playlistdetails

import com.example.navbottomtest.R


class PlaylistAdapter(private  val playlistList:Map<String,List<Int>>):
    RecyclerView.Adapter<PlaylistAdapter.MyViewHolder>() {
    inner class MyViewHolder(private  val playlistView: View):
        RecyclerView.ViewHolder(playlistView){
            private  val playListName=playlistView.findViewById<TextView>(R.id.txtPlaylistName)

        fun  bindData(playlist:String){
            playListName.text=playlist
            playlistView.setOnClickListener {
                println("Clicked on playlist with name ${playListName.text} and  its values are $playlistList ")
//                println(playlistList[playListName.text])
                val songs= mapOf(playListName.text.toString() to (playlistList[playListName.text.toString()]?: emptyList()))

                playlistdetails= songs
                //TODO either send playlist name or the entire playlistList with ids maybe filter them
//                val songs=playlistList[playListName.text] ?: emptyList()
                println("song ids of given id is $songs")
                val context=playlistView.context
                context.startActivity(Intent(context,PlaylistDetails::class.java))
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val playlistview=LayoutInflater.from(parent.context).inflate(R.layout.playlist_recycler,parent,false)
        return  MyViewHolder(playlistview)
    }

    override fun getItemCount(): Int {
        return  playlistList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val playlistname=playlistList.keys.toList()[position]
        holder.bindData(playlistname)
    }
}