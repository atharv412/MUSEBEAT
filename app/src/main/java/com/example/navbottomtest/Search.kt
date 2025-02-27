package com.example.navbottomtest

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.navbottomtest.adapter.SearchAdapter
import com.example.navbottomtest.models.CategoryModel
import com.example.navbottomtest.models.SongModel
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
    private val supaClient=SupabaseClientProvider.client
    private  var searchJob:Job?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val rootview=inflater.inflate(R.layout.search_fragment, container, false)

        val searchInput=rootview.findViewById<EditText>(R.id.search_bar)
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                println("beforeTextChanged implementation")

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                println(s)
                getSearchSongs(rootview,s.toString())

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
            }
        })

        return rootview
    }

    private fun getSearchSongs(rootView: View,query:String)
    {
        CoroutineScope(Dispatchers.IO).launch {
            val responses = supaClient.from("songs")
                .select {
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

