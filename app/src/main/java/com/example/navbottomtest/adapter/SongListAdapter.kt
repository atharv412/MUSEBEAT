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
import com.example.navbottomtest.SupabaseClientProvider
import com.example.navbottomtest.models.SongModel
import com.example.navbottomtest.models.UserModel
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SongListAdapter(private  val songList:List<SongModel>):
    RecyclerView.Adapter<SongListAdapter.MyViewHolder>()
    {

        class MyViewHolder(private  val songView:View):
            RecyclerView.ViewHolder(songView)
        {
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

                        val session = SupabaseClientProvider.client.auth

                        println("adding song${song.id} and ${song.song_name} to user history")
                        CoroutineScope(Dispatchers.IO).launch {
                            appendToUserHistory(session.currentSessionOrNull()?.user?.email.toString(),song.id.toLong())
                        }

                        it.context.startActivity(Intent(it.context,SongPlayer::class.java))
                    }
                }

            private suspend fun appendToUserHistory(userId: String, newValue: Long) {
                // Fetch the existing history
                val supaClient= SupabaseClientProvider.client


                val response = supaClient.from("user")
                    .select {
                        filter {
                            eq("user_email",userId )
                        }
                    }
                    .decodeSingle<UserModel>()
                println("user is "+response.user_email)

                val existingHistory: List<Int> =response.user_history

//                    response.user_history.getList("user_history", Long::class.java)
                println("user's  current history "+existingHistory)

                // Ensure it's not null and append the new value
                val updatedHistory = (existingHistory ?: emptyList()) + newValue
                println("user's  updated history "+updatedHistory)

                val res=supaClient.from("user")
                    .update{mapOf("user_history" to updatedHistory)
                        filter {
                            this@update.select()
                            eq("user_email",userId )
                        }
                    }
                println("check db for verification ${res.data}")
                // Update the database
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


//     suspend fun appendToUserHistory(userId: Long, newValue: Long) {
//            // Fetch the existing history
//         val supaClient= SupabaseClientProvider.client
//
//
//            val response = supaClient.from("user")
//                .select(columns = Columns.list("user_history")){
//                    filter {
//                        eq("user_email",userId )
//                    }
//                }
//                .decodeSingle<UserModel>()
//
//         val existingHistory: List<Long> =response.user_history
//
////                    response.user_history.getList("user_history", Long::class.java)
//
//         // Ensure it's not null and append the new value
//         val updatedHistory = (existingHistory ?: emptyList()) + newValue
//
//         supaClient.from("user")
//             .update{mapOf("user_history" to updatedHistory)
//                 filter {
//                     eq("id", supaClient.auth)
//
//                 }
//             }
//         // Update the database
//     }

    }