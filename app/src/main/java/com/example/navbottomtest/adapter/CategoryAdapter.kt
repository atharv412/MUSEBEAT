package com.example.navbottomtest.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.RoundedCorner
import android.view.View
import android.view.ViewGroup
import android.view.ViewManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.navbottomtest.R
import com.example.navbottomtest.SongsList
import com.example.navbottomtest.models.CategoryModel

class CategoryAdapter(private val categoryList:List<CategoryModel>) :
    RecyclerView.Adapter<CategoryAdapter.MyViewHolder>(){

    class MyViewHolder(private  val itemView: View ) :
        RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.name_text_view)
        private  val coverImageView:ImageView=itemView.findViewById(R.id.cover_image_view)

        fun bindData(category: CategoryModel){
            nameTextView.text=category.name
            Glide.with(coverImageView).load(category.imageurl)
                .apply(
                    RequestOptions().transform(RoundedCorners(32))
                )
                .into(coverImageView)

            val context=itemView.context
            coverImageView.setOnClickListener{
                SongsList.category=category
                context.startActivity(Intent(context,SongsList::class.java))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemview=LayoutInflater.from(parent.context).inflate(R.layout.category_recycler_row_item,parent,false)
        return MyViewHolder(itemview)
    }

    override fun getItemCount(): Int {
        return  categoryList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindData(categoryList[position])
    }
}