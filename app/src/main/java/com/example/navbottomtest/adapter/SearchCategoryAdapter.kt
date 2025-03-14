package com.example.navbottomtest.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.navbottomtest.models.CategoryModel
import com.example.navbottomtest.R

class SearchCategoryAdapter(
    private val categoryList: List<CategoryModel>,
    private val onCategoryClick:(String)->Unit
): RecyclerView.Adapter<SearchCategoryAdapter.MyViewHolder>(){
    inner  class MyViewHolder(private val itemView:View ):
        RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.search_category_title)
        private val coverImageView: ImageView =
            itemView.findViewById(R.id.search_category_cover_image)

        fun bindData(searchCategory: CategoryModel) {
            nameTextView.text=searchCategory.name
            Glide.with(coverImageView).load(searchCategory.imageurl)
                .apply(
                    RequestOptions().transform(RoundedCorners(32))
                )
                .into(coverImageView)
            itemView.setOnClickListener {
                onCategoryClick(searchCategory.name)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemview=LayoutInflater.from(parent.context).inflate(R.layout.search_categories_recycler,parent,false)
        return  MyViewHolder(itemview)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindData(categoryList[position])
    }
}