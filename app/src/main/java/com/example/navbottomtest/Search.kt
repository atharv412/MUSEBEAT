package com.example.navbottomtest

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.navbottomtest.adapter.SearchAdapter
import com.example.navbottomtest.adapter.SearchCategoryAdapter
import com.example.navbottomtest.adapter.UserHistoryAdapter
import com.example.navbottomtest.models.CategoryModel
import com.example.navbottomtest.models.SongHistoryModel
import com.example.navbottomtest.models.SongModel
import com.example.navbottomtest.models.UserModel
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Search:Fragment() {

    private  lateinit var searchSongAdapter:SearchAdapter
    private lateinit var searchCategoryAdapter: SearchCategoryAdapter
    private val supaClient=SupabaseClientProvider.client
    private  var searchJob:Job?=null
    private lateinit var username:String
    private lateinit var userHistoryAdapter: UserHistoryAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val rootview=inflater.inflate(R.layout.search_fragment, container, false)

        val searchInput=rootview.findViewById<EditText>(R.id.search_bar)
        val greetText=rootview.findViewById<TextView>(R.id.searchWelcome)
//        }
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                println("beforeTextChanged implementation")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                println(s)
                val userHistoryRecycler = rootview.findViewById<RecyclerView>(R.id.userSearchHistory)
                val tempTitle=rootview.findViewById<TextView>(R.id.temp_text)
                if (s.isNullOrEmpty()) {
                    // Show user history when input is empty
                    userHistoryRecycler.visibility = View.VISIBLE
                    tempTitle.visibility=View.VISIBLE
                } else {
                    // Hide user history when typing starts
                    userHistoryRecycler.visibility = View.GONE
//                    tempTitle.visibility=View.GONE
                    getSearchSongs(rootview, s.toString())
                }
//                getSearchSongs(rootview,s.toString())
            }
            override fun afterTextChanged(s: Editable?) {
                println("afterTextChanged implementation"+s)
                searchJob?.cancel()
                searchJob=CoroutineScope(Dispatchers.Main).launch {
                    delay(2500)
                    if (s.toString().startsWith("songs:")) {
                        val category = s.toString().split(":")[1].trim().orEmpty()
                        println(category)
                        getSearchCategorySongs(rootview, category)
                    }
                    //replicate this for
                }
//                val serchTopRecycler=rootview.findViewById<GridLayout>(R.id.searchGrid)
//
//                serchTopRecycler.visibility=View.GONE
            }
        })

        CoroutineScope(Dispatchers.IO).launch {
            val userName=supaClient.auth.retrieveUserForCurrentSession(updateSession = true)
            withContext(Dispatchers.Main){
                greetText.text=userName.email
                username=userName.email.toString()
            }
        }

        getUserSearchHistory(rootview)
        getSearchCategories(rootview)
        return rootview
    }

    private fun getSearchSongs(rootView: View,query:String)
    {
        CoroutineScope(Dispatchers.IO).launch {
            val responses = supaClient.from("songs").select {
                    filter {
                        or {
                        ilike("song_name", "%$query%")
                        ilike("artist_name","%$query%")
                        ilike("song_movie","%$query%")
                        }
                    }
                }
                .decodeList<SongModel>()//TODO: create a model for the song or use the song model
                println(responses)
            withContext(Dispatchers.Main){
                //TODO: call the setupSearchAdapterRecyclerView() to setup adapter to the view
                setupSearchAdapterRecyclerView(responses,rootView)
                //TODO :send the response as a parameter
            }
        }
    }
    private fun getSearchCategorySongs(rootView: View,query: String){
        CoroutineScope(Dispatchers.IO).launch {
            val categoryName=supaClient.from("category").select{
                filter { ilike("name" , "%$query%") }
            }.decodeSingleOrNull<CategoryModel>()

            if (categoryName!=null){
                val responses=supaClient.from("songs").select {
                    filter { eq("song_category" , categoryName.id) }
                }.decodeList<SongModel>()
                println(responses)
                withContext(Dispatchers.Main){
                    setupSearchAdapterRecyclerView(responses,rootView)
                }
            }else{
                println("category does not match any value")
            }

        }
    }

    private  fun getSearchCategories(rootView: View){
        CoroutineScope(Dispatchers.IO).launch {
            val categories = supaClient
                .from("category")
                .select()
                .decodeList<CategoryModel>()
//            Log.d("atharva", "list: ${cat1.data}")

            withContext(Dispatchers.Main) {
//                Log.d("atharva", "list is set: ${categories.size}")
                setupSearchCategories(rootView,categories)

            }
        }
    }
    private  fun setupSearchCategories(rootView: View,categoryList: List<CategoryModel>){
        val recyclerView=rootView.findViewById<RecyclerView>(R.id.search_category)
        searchCategoryAdapter=SearchCategoryAdapter(categoryList){ categoryName->
            searchSongsByCategory(rootView, categoryName )
        }
        recyclerView.layoutManager=GridLayoutManager(context,2)
        recyclerView.adapter=searchCategoryAdapter
    }
    private fun searchSongsByCategory(rootView: View,categoryName:String){
        val searchBar=rootView.findViewById<EditText>(R.id.search_bar)
        searchBar.setText("songs:${categoryName}")
    }

    private fun getUserSearchHistory(rootView: View){
        CoroutineScope(Dispatchers.IO).launch {

            val currentUser = supaClient.auth.currentSessionOrNull()?.user?.email.toString()

            val response = supaClient.from("user")
                .select {
                    filter {
                        eq("user_email", currentUser)
                    }

                }
                .decodeSingle<UserModel>()

            val userHistory = supaClient.from("user_history").select(columns = Columns
                .raw("id,user_id,song_id,songs(id,song_name,song_url,artist_name,song_movie,song_releasing_date,song_category,cover_image)".trimIndent())
            ) {
                filter {
                    eq("user_id", response.id.toString())
                }
            }.decodeList<SongHistoryModel>()

            val songIds = userHistory.map { it.song_id }.reversed().take(10)
            println("songsbased on song_id of user_history$songIds")

            if (songIds.isNotEmpty()) {
                val songs = songIds.map { songId ->
                    supaClient.from("songs")
                        .select {
                            filter { eq("id", songId) }
                        }
                        .decodeSingle<SongModel>() // Fetch one song at a time
                }
                withContext(Dispatchers.Main){
                    setupUserSearchHistory(rootView,songs)
                }
            }
        }
    }
    private  fun setupUserSearchHistory(rootView: View,userHistorySongList:List<SongModel>){
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.userSearchHistory)
        userHistoryAdapter = UserHistoryAdapter(userHistorySongList)
        recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = userHistoryAdapter

    }
    private fun setupSearchAdapterRecyclerView(searchedSongs:List<SongModel>,rootView: View){
        val recyclerView=rootView.findViewById<RecyclerView>(R.id.search_song_recycler)
        //TODO: create a search adapter like the category adapter
        searchSongAdapter=SearchAdapter(searchedSongs)
//        categoryAdapter=SearchAdapter(searchedSongs) //gets the list of rows from the getCategories()
// TODD: like this
        recyclerView.layoutManager=LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        recyclerView.adapter=searchSongAdapter
        Log.d("atharva", "Adapter is set: ${recyclerView.adapter!=null}")

        // TODO: test this, may do changes regarding case sensitivity //success
    }
}

