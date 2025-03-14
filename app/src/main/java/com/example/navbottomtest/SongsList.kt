package com.example.navbottomtest

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.navbottomtest.adapter.SongListAdapter
import com.example.navbottomtest.models.CategoryModel
import com.example.navbottomtest.models.SongModel
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SongsList:AppCompatActivity() {

    companion object{
        lateinit var category: CategoryModel
    }

    private  lateinit var songListAdapter:SongListAdapter
    private val supaClient=SupabaseClientProvider.client
//    private val cat=SupabaseClientProvider.client

    override fun onCreate(savedInstanceState: Bundle?) {
        //TODO inflate fragment by the the songlist_layout
        //TODO write the the songlist adapter and connect to this activity
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_songs_list)

        val nameTextView=findViewById<TextView>(R.id.name_text_view2)
        val coverImageView=findViewById<ImageView>(R.id.cover_image_view2)
        val back=findViewById<ImageButton>(R.id.backButton)
        nameTextView.text= category.name


        Glide.with(coverImageView).load(category.imageurl)
            .apply(
                RequestOptions().transform(RoundedCorners(32))
            )
            .into(coverImageView)

        getSongsOnCategory()

        back.setOnClickListener{
            finish()
        }
    }
    fun getSongsOnCategory(){
        val categoryID:Int= category.id
        CoroutineScope(Dispatchers.IO).launch {
            val songs=supaClient
                .from("songs")
                .select().decodeList<SongModel>()
                //columns = Columns.raw("id,song_name,song_url,artist_name,song_releasing_date,song_category(id)")
//                .equals("song_category")
            Log.d("atharva", "list: ${songs}")

            withContext(Dispatchers.Main){
                val songsByCategory=songs.filter { it.song_category== categoryID}
                setupSongsRecyclerView(songsByCategory)
            }
        }
    }

    fun setupSongsRecyclerView(songList: List<SongModel>){
        val rowRecyclerView=findViewById<RecyclerView>(R.id.songs_list_recycler)
        songListAdapter=SongListAdapter(songList)//define the songlist adapter
        rowRecyclerView.layoutManager=LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        rowRecyclerView.adapter=songListAdapter
        Log.d("atharva", "Adapter is set: ${rowRecyclerView.adapter!=null}")

    }
}