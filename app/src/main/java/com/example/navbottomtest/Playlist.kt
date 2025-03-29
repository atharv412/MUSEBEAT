package com.example.navbottomtest

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.media3.common.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.navbottomtest.adapter.CreatePlayListAdapter
import com.example.navbottomtest.adapter.PlaylistAdapter
import com.example.navbottomtest.models.CategoryModel
import com.example.navbottomtest.models.PlaylistModel
import com.example.navbottomtest.models.SongModel
import com.example.navbottomtest.models.UserModel
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Count
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Playlist:Fragment() {

    private val supaClient = SupabaseClientProvider.client
    private lateinit var songSelectionAdapter: CreatePlayListAdapter
    private lateinit var playlistAdapter:PlaylistAdapter
    private var cachedShuffledSongs: List<SongModel> = emptyList()
    companion object{
        lateinit var PlaylistTopSlider: SongModel
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootview=inflater.inflate(R.layout.playlists_fragment,container,false)

        val playlistcreator=rootview.findViewById<Button>(R.id.btnGenerate)

        getPlaylists(rootview)


        playlistcreator.setOnClickListener {
            showCreatePlaylistDialog(rootview)
        }

        return rootview
    }


    private fun showCreatePlaylistDialog(rootView: View) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.playlist_creator, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        val editTextPlaylistName = dialogView.findViewById<EditText>(R.id.editPlaylistName)
        val btnCreate = dialogView.findViewById<Button>(R.id.btnCreatePlaylist)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)
        // Load Songs into RecyclerView (Optional: Let user select songs)
        val playlistSearchInput=dialogView.findViewById<EditText>(R.id.search_song_for_playlist);

            getSongsForPlaylists(dialogView)

            playlistSearchInput.addTextChangedListener(object :TextWatcher{
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                   println(p0)
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    println("text when finding songs from playlist${s}")
                    if(s.isNullOrEmpty())
                    {
                        setupSongSelector(cachedShuffledSongs,dialogView)
                    }
                    else{
                        getSearchSongsForPlaylist(dialogView,s.toString())

                    }
                }
                override fun afterTextChanged(p0: Editable?) {
                    println(p0)
                }

            })
        // Handle Create Button Click
        btnCreate.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {

                val playlistName = editTextPlaylistName.text.toString().trim()

                val selectedSongs=songSelectionAdapter.getSelectedSongs()
                println("selected songs are as follows ${selectedSongs.map { it.song_name }}")

                if (playlistName.isNotEmpty()) {
                    createPlaylist(playlistName, selectedSongs)
                    dialog.dismiss()
                } else {
//                    Toast.makeText(context, "Enter a valid playlist name", Toast.LENGTH_SHORT).show()
                    println("Enter a valid playlist name")
                }
            }
        }

        // Handle Cancel Button Click
        btnCancel.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }



    private  fun getPlaylists(rootView: View){
        CoroutineScope(Dispatchers.IO).launch {
            val currentUser = supaClient.auth.currentSessionOrNull()?.user?.email.toString()

            val response = supaClient.from("user")
                .select {
                    filter {
                        eq("user_email", currentUser)
                    }

                }
                .decodeSingle<UserModel>()

            val playlists = supaClient.from("user_playlist")
                .select {
                    filter {
                        eq("user_id", response.id.toString())
                    }
                }
                .decodeList<PlaylistModel>()

            //METHOD 1
            val groupedPlaylists = playlists
                .groupBy { it.playlist_name }
                .mapValues { it.value.map { song -> song.song_id } }

            //METHOD 2
//            val groupedPlaylists=playlists
//                .groupBy { it.playlist_name }
//            val simplifiedPlaylists = groupedPlaylists
//                .mapValues { (_, songs) ->
//                    songs.map { it.song_id }
//                }
                println("the grouped  list is $groupedPlaylists")
            withContext(Dispatchers.Main){
                setupPlaylistRecyclerView(rootView,groupedPlaylists)
            }

        }
    }

    private fun setupPlaylistRecyclerView(rootView: View,playlistNameList:Map<String,List<Int>>){
        val recyclerView=rootView.findViewById<RecyclerView>(R.id.recyclerViewPlaylists)
        playlistAdapter=PlaylistAdapter(playlistNameList)
        recyclerView.layoutManager=GridLayoutManager(context,3)
        recyclerView.adapter=playlistAdapter

    }

    private  fun getSongsForPlaylists(rootView: View){
        CoroutineScope(Dispatchers.IO).launch {
            val songs=supaClient.from("songs")
                .select {
                    limit(50)
                }
                .decodeList<SongModel>()
//            println("Songs for playlist are as follows $songs")
            withContext(Dispatchers.Main){
                cachedShuffledSongs=songs.shuffled().take(10)
                val shuffledSongs:List<SongModel> = songs.shuffled().take(10)
                setupSongSelector(shuffledSongs,rootView)
            }
        }
    }

    private fun getSearchSongsForPlaylist(rootView: View, query:String)
    {
        CoroutineScope(Dispatchers.IO).launch {
            val responses = supaClient.from("songs").select {
                filter {
                    or {
                        ilike("song_name", "%$query%")
                        ilike("artist_name", "%$query%")
                        ilike("song_movie", "%$query%")
                    }
                }
            }
                .decodeList<SongModel>()//TODO: create a model for the song or use the song model
            println(responses)
            withContext(Dispatchers.Main){
                //TODO: call the setupSearchAdapterRecyclerView() to setup adapter to the view
                setupSongSelector(responses,rootView)
                //TODO :send the response as a parameter
            }
        }
    }


    private fun setupSongSelector(songsList: List<SongModel>, rootView: View){
        val recyclerViewSongs = rootView.findViewById<RecyclerView>(R.id.recyclerViewSongs)
        songSelectionAdapter=CreatePlayListAdapter(songsList)
        recyclerViewSongs.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        recyclerViewSongs.adapter=songSelectionAdapter
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun createPlaylist(playlistName:String, selectedSongs:List<SongModel>){
        val selectedSongsId=selectedSongs.map { it.id }
        CoroutineScope(Dispatchers.IO).launch {
            val currentUser = supaClient.auth.currentSessionOrNull()?.user?.email.toString()

            val response = supaClient.from("user")
                .select {
                    filter {
                        eq("user_email", currentUser)
                    }
                }
                .decodeSingle<UserModel>()
            val id=response.id
            val columns= selectedSongsId.map {songId->
                PlaylistModel(
                    playlist_name = playlistName,
                    song_id = songId,
                    user_id = id)
            }
            println(columns)
            Log.d("atharva","values to be inserted are as follows $columns")

            val createdPLaylist=supaClient
                .from("user_playlist")
                .insert(columns)

            if (createdPLaylist.data.isNotEmpty()){
                Log.d("atharva","playlist created ")
            }
        }
    }

}