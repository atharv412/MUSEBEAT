package com.example.navbottomtest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView

class Playlist:Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootview=inflater.inflate(R.layout.playlists_fragment,container,false)

        val playlistcreator=rootview.findViewById<Button>(R.id.btnGenerate)




        playlistcreator.setOnClickListener {
            showCreatePlaylistDialog()
        }

        return rootview
    }



    private fun showCreatePlaylistDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.playlist_creator, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        val editTextPlaylistName = dialogView.findViewById<EditText>(R.id.editPlaylistName)
        val recyclerViewSongs = dialogView.findViewById<RecyclerView>(R.id.recyclerViewSongs)
        val btnCreate = dialogView.findViewById<Button>(R.id.btnCreatePlaylist)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)

        // Load Songs into RecyclerView (Optional: Let user select songs)
//        recyclerViewSongs.layoutManager = LinearLayoutManager(this)
//        val songAdapter = SongSelectionAdapter(getAllSongs()) // Implement song selection adapter
//        recyclerViewSongs.adapter = songAdapter

        // Handle Create Button Click
//        btnCreate.setOnClickListener {
//            val playlistName = editTextPlaylistName.text.toString().trim()
//            val selectedSongs = songAdapter.getSelectedSongs() // Get selected songs
//
//            if (playlistName.isNotEmpty()) {
//                createPlaylist(playlistName, selectedSongs)
//                dialog.dismiss()
//            } else {
//                Toast.makeText(this, "Enter a valid playlist name", Toast.LENGTH_SHORT).show()
//            }
//        }

        // Handle Cancel Button Click
        btnCancel.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }

}