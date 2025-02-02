package com.example.navbottomtest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.navbottomtest.adapter.CategoryAdapter
import com.example.navbottomtest.models.CategoryModel
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Home:Fragment() {

    private lateinit var categoryAdapter: CategoryAdapter
    private val supaClient=SupabaseClientProvider.client
    private val cat=SupabaseClientProvider.client
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootview=inflater.inflate(R.layout.home_fragment, container, false)
//
        val settingsbtn=rootview.findViewById<ImageView>(R.id.settings_icon)

        settingsbtn.setOnClickListener {
            val intent= Intent(context,Settings::class.java)
            startActivity(intent)
        }
        getCategories(rootview)
//        Log.d("atharva", "Adapter is set: ${rootview.findViewById<RecyclerView>(R.id.categories_recycler_view).adapter}")

        return  rootview
    }
    private fun getCategories(rootView: View){
        CoroutineScope(Dispatchers.IO).launch {
            val categories=supaClient
                .from("category")
                .select()
                .decodeList<CategoryModel>()
//            val cat1=supaClient.from("category").select()
//            Log.d("atharva", "list: ${cat1.data}")

            withContext(Dispatchers.Main){
//                Log.d("atharva", "list is set: ${categories.size}")
                setupCategoryRecyclerView(categories,rootView)
            }
        }
    }
    private fun setupCategoryRecyclerView(categoryList: List<CategoryModel>, rootView:View){
        val recyclerView=rootView.findViewById<RecyclerView>(R.id.categories_recycler_view)
        categoryAdapter=CategoryAdapter(categoryList) //gets the list of rows from the getCategories()
        recyclerView.layoutManager=LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)//changes the layout to horizontal
//        Log.d("atharva", "Adapter is set: ${recyclerView.adapter!=null}")

        recyclerView.adapter=categoryAdapter
//        Log.d("atharva", "Adapter is set: ${recyclerView.adapter!=null}")
    }
}