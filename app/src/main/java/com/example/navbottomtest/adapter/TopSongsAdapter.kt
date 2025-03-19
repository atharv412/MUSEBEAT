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
import com.example.navbottomtest.models.SongHistoryModel
import com.example.navbottomtest.models.SongModel
import com.example.navbottomtest.models.UserModel
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TopSongsAdapter(private  val topSongs:List<SongModel>):
    RecyclerView.Adapter<TopSongsAdapter.MyViewHolder>()
{
    class  MyViewHolder(private  val topSongsView:View):
        RecyclerView.ViewHolder(topSongsView) {

        private val songName: TextView = itemView.findViewById(R.id.music_title)
        private  val songCoverImage: ImageView =itemView.findViewById(R.id.music_image_1)
        private val client= SupabaseClientProvider.client


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

                CoroutineScope(Dispatchers.IO).launch {
                    val email=client.auth.currentSessionOrNull()?.user?.email.toString()
                    appendToUserHistory(email,topSong.id)
                }
            }
        }
        private suspend fun appendToUserHistory(userEmail: String, newValue: Int) {
            // Fetch the existing history
            val response = client.from("user")
                .select {
                    filter {
                        eq("user_email",userEmail )
                    }
                }
                .decodeSingle<UserModel>()
            println("user is "+response.user_email)

            val userid=response.id!!
            val song=newValue
//            val columns = SongHistoryModel(user_id = userid, song_id = song)
            val columns= mapOf(
                "user_id" to userid,
                "song_id" to song
            )
            println("updating user history with id${userid} with song id ${song}")

            CoroutineScope(Dispatchers.IO).launch {
                val exisitingHistory=client.from("user_history").select{
                    filter {
                        and {
                            eq("user_id", userid)
                            eq("song_id", song)
                        }
                    }
                }
                    println("song is ${ exisitingHistory }")
                if (exisitingHistory.data.isNotEmpty()){
                    try {
                        val updatedHistory=client.from("user_history").insert(columns)
                        if (!updatedHistory.data.isEmpty()){
                            println("song is $exisitingHistory")
                            println("${updatedHistory}song added to userHistory")
                        }

                    }catch (e: Exception){
                        println(e.message)
                    }

                }
                else if (exisitingHistory.data.isNotEmpty()){
                    withContext(Dispatchers.Main){
                        println("song already in history")
                    }
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val topsongsview=LayoutInflater.from(parent.context).inflate(R.layout.music_card2,parent,false)
        return  MyViewHolder(topsongsview)
    }

    override fun getItemCount(): Int {
        return  topSongs.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindData(topSongs[position])
    }
}