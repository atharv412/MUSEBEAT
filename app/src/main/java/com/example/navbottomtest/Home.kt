package com.example.navbottomtest

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.Manifest
import android.annotation.SuppressLint
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.navbottomtest.adapter.CategoryAdapter
import com.example.navbottomtest.adapter.OfflineSongsAdapter
import com.example.navbottomtest.adapter.TopSongsAdapter
import com.example.navbottomtest.adapter.TrendingSongsAdapter
import com.example.navbottomtest.adapter.UserHistoryAdapter
import com.example.navbottomtest.models.CategoryModel
import com.example.navbottomtest.models.OfflineSongsModel
import com.example.navbottomtest.models.SongHistoryModel
import com.example.navbottomtest.models.SongModel
import com.example.navbottomtest.models.UserModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.navbottomtest.Playlist.Companion.PlaylistTopSlider
import com.example.navbottomtest.adapter.PopularSongAdapter

class Home:Fragment() {

    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var topSongsAdapter: TopSongsAdapter
    private lateinit var offlineSongsAdapter: OfflineSongsAdapter
    private lateinit var userHistoryAdapter: UserHistoryAdapter
    private lateinit var trendingSongsAdapter:TrendingSongsAdapter
    private lateinit var popularSongAdapter: PopularSongAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private val supaClient = SupabaseClientProvider.client
    private val cat = SupabaseClientProvider.client
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted, now fetch audio files
            getAudioFiles(requireContext(), requireView())
        } else {
            Toast.makeText(requireContext(), "Permission denied!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootview = inflater.inflate(R.layout.home_fragment, container, false)
//
        val settingsbtn = rootview.findViewById<ImageView>(R.id.settings_icon)
        val greetText=rootview.findViewById<TextView>(R.id.home_greetingtxt)
        val homeSearch=rootview.findViewById<LinearLayout>(R.id.home_searchBar)
        swipeRefreshLayout=rootview.findViewById<SwipeRefreshLayout>(R.id.mainSwipeRefreshLayout)

        CoroutineScope(Dispatchers.IO).launch {
            val userName=supaClient.auth.retrieveUserForCurrentSession(updateSession = true).email.toString()
            withContext(Dispatchers.Main){
                greetText.text="Hello ${userName}"
            }
        }

        homeSearch.setOnClickListener {
            val searchFrag = Search()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, searchFrag)
//                .addToBackStack(null)
                .commit()
            val menuItems = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            menuItems.selectedItemId = R.id.navSearch
        }

        settingsbtn.setOnClickListener {
            val intent = Intent(context, Settings::class.java)
            startActivity(intent)

        }

        swipeRefreshLayout.setOnRefreshListener {
            requestAudioPermissions()
            getCategories(rootview)
            getTopSongs(rootview)
            getTrendingSongs(rootview)
            getAudioFiles(requireContext(), rootview)
            getUserHistory(rootview)

            swipeRefreshLayout.isRefreshing=false
        }
//        todo implement refresh frag on overscroll feature for this fragment//COMPLETED
        requestAudioPermissions()
        getCategories(rootview)
        getTopSongs(rootview)
        getTrendingSongs(rootview)
        getAudioFiles(requireContext(), rootview)
        getUserHistory(rootview)
//        Log.d("atharva", "Adapter is set: ${rootview.findViewById<RecyclerView>(R.id.categories_recycler_view).adapter}")

        return rootview
    }

    private fun getCategories(rootView: View) {
        CoroutineScope(Dispatchers.IO).launch {
            val categories = supaClient
                .from("category")
                .select()
                .decodeList<CategoryModel>()
//            Log.d("atharva", "list: ${cat1.data}")

            withContext(Dispatchers.Main) {
//                Log.d("atharva", "list is set: ${categories.size}")
                setupCategoryRecyclerView(categories, rootView)
            }
        }
    }

    private fun setupCategoryRecyclerView(categoryList: List<CategoryModel>, rootView: View) {
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.categories_recycler_view)
        categoryAdapter = CategoryAdapter(categoryList) //gets the list of rows from the getCategories()
        recyclerView.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )//changes the layout to horizontal
//        Log.d("atharva", "Adapter is set: ${recyclerView.adapter!=null}")
        recyclerView.adapter = categoryAdapter
//        Log.d("atharva", "Adapter is set: ${recyclerView.adapter!=null}")
    }

    private fun getTopSongs(rootView: View) {
        CoroutineScope(Dispatchers.IO).launch {
            val topsongs = supaClient.from("songs")
                .select {
//                    order("RANDOM()", order = Order.ASCENDING)
                    limit(50)
                }
                .decodeList<SongModel>()
            println(topsongs)

            withContext(Dispatchers.Main) {

                val randomSongs = topsongs.shuffled().take(10)
                println("random songs are as follows" + randomSongs)
                val sliderSong= topsongs.shuffled().take(1)[0]
                println("the song for the top song is "+sliderSong)
                PlaylistTopSlider=topsongs.shuffled().take(1)[0]
                setupTopSlider(sliderSong,rootView)
                setupTopsongsRecyclerView(randomSongs, rootView)
            }
        }
    }

    private fun setupTopsongsRecyclerView(topSongsList: List<SongModel>, rootView: View) {
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.top_songs_recycler)
        topSongsAdapter = TopSongsAdapter(topSongsList)
        recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = topSongsAdapter
    }

    private  fun getTrendingSongs(rootView: View){
        CoroutineScope(Dispatchers.IO).launch {
            val trendingsongs=supaClient.from("songs")
                .select{
                    limit(50)
                    order("artist_name", order = Order.ASCENDING,nullsFirst = true)

                }
                .decodeList<SongModel>()
            println("the trending sonngs are a:::::"+trendingsongs)
            withContext(Dispatchers.Main){
                val trending=trendingsongs.take(10)
                val popular=trendingsongs.shuffled().take(10)
                println("trending songs are as follows" + trending)
                println("popular songs are as follows" + popular)
                setupPopularSongsRecyclerView(popular,rootView)
                setupTrendingSongsRecyclerView(trending,rootView)
            }
        }
    }

    private  fun setupTrendingSongsRecyclerView(trendingSongsList:List<SongModel>,rootView: View){
        val recyclerView=rootView.findViewById<RecyclerView>(R.id.trending_songs_recycler)
        trendingSongsAdapter=TrendingSongsAdapter(trendingSongsList)
        recyclerView.layoutManager=LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        recyclerView.adapter=trendingSongsAdapter
    }

    private fun setupPopularSongsRecyclerView(popularSongsList:List<SongModel>,rootView: View){
        val recyclerView=rootView.findViewById<RecyclerView>(R.id.popular_songs_recycler)
        popularSongAdapter= PopularSongAdapter(popularSongsList)
        recyclerView.layoutManager=LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        recyclerView.adapter=popularSongAdapter
    }

    private fun setupTopSlider(sliderSong:SongModel,rootView: View) {
        val sliderImage=rootView.findViewById<ImageView>(R.id.slider_image)
        val sliderSongName=rootView.findViewById<TextView>(R.id.tv_title)
        val sliderSongArtist=rootView.findViewById<TextView>(R.id.tv_subtitle)
        val sliderPlayButton=rootView.findViewById<Button>(R.id.btn_play)

        sliderSongName.text=sliderSong.song_name
        sliderSongArtist.text=sliderSong.artist_name
        Glide.with(sliderImage).load(sliderSong.cover_image)
            .apply(
                RequestOptions().transform(RoundedCorners(32))
            )
            .into(sliderImage)

        sliderPlayButton.setOnClickListener {
            Exoplayer.startSong(it.context,sliderSong)
            it.context.startActivity(Intent(it.context, SongPlayer::class.java))
        }
    }

    private fun getAudioFiles(context: Context, rootView: View) {
        val songList = mutableListOf<OfflineSongsModel>()
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media.DATA, // File path
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST
        )

        // Selection to filter only MP3 files
        val selection = "${MediaStore.Audio.Media.MIME_TYPE} = ?"
        val selectionArgs = arrayOf("audio/mpeg") // MIME type for MP3 files

        val cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)

        cursor?.use {
            val dataColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val titleColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
//            val coverImageColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)

            while (it.moveToNext()) {
                val path = it.getString(dataColumn)
                val title = it.getString(titleColumn)
                val artist = it.getString(artistColumn)

                songList.add(OfflineSongsModel(title, path))
//                if (path.contains("/Download/", ignoreCase = true) && path.endsWith(".mp3", ignoreCase = true)) {
//                }
            }
        }

        println("OFFLINE SONGS FOUND: $songList")

        // Setup RecyclerView
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.offline_songs_recycler)
        offlineSongsAdapter = OfflineSongsAdapter(songList)
        recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = offlineSongsAdapter
        println("Adapter is set: ${recyclerView.adapter != null}")
    }

    private fun requestAudioPermissions() {
        val permission = Manifest.permission.READ_MEDIA_AUDIO
        requestPermissionLauncher.launch(permission)
    }

    @SuppressLint("SuspiciousIndentation")
    private fun getUserHistory(rootView: View) {
        CoroutineScope(Dispatchers.IO).launch {
            val currentUser = supaClient.auth.currentSessionOrNull()?.user?.email.toString()

            val response = supaClient.from("user")
                .select {
                    filter {
                        eq("user_email", currentUser)
                    }

                }
                .decodeSingle<UserModel>()

            val userHistory = supaClient.from("user_history").select(
                columns = Columns
                    .raw("id,user_id,song_id,songs(id,song_name,song_url,artist_name,song_movie,song_releasing_date,song_category,cover_image)".trimIndent())
            ) {
                filter {
                    eq("user_id", response.id.toString())

                }
            }.decodeList<SongHistoryModel>()
            //[SongHistoryModel(id=2, user_id=5, song_id=1), SongHistoryModel(id=3, user_id=5, song_id=6), SongHistoryModel(id=4, user_id=5, song_id=13)]

            if (userHistory.isEmpty()) {
                withContext(Dispatchers.Main) {
                    val recyclerView =
                        rootView.findViewById<RecyclerView>(R.id.recently_played_recycler)
                    val emptyTextView = rootView.findViewById<TextView>(R.id.empty_history_message)
                    emptyTextView.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                }

            } else {
                println(userHistory)
                val songIds = userHistory.map { it.song_id }.reversed().take(9)
                println("songsbased on song_id of user_history$songIds")

                if (songIds.isNotEmpty()) {
                    val songs = songIds.map { songId ->
                        supaClient.from("songs")
                            .select {
                                filter { eq("id", songId) }
                            }
                            .decodeSingleOrNull<SongModel>() // Fetch one song at a time
                    }
                    println("songs fetched  from user_history" + songs)
                    withContext(Dispatchers.Main) {

                        setupUserHistory(songs, rootView)
                    }
                }
            }
        }
    }

    private fun setupUserHistory(userHistorySongList: List<SongModel?>, rootView: View){
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.user_history_recycler)
        userHistoryAdapter = UserHistoryAdapter(userHistorySongList)
        recyclerView.layoutManager =
            GridLayoutManager(context, 3,LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = userHistoryAdapter

    }
}