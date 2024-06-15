package com.isyaratku.app.ui.main.home

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.isyaratku.app.R
import com.isyaratku.app.api.ItemNews

class NewsAdapter : ListAdapter<ItemNews,NewsAdapter.MyViewHolder>(DiffCallback()) {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =  LayoutInflater.from(parent.context).inflate(R.layout.news_layout,parent,false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position))
        val animation = AnimationUtils.loadAnimation(holder.itemView.context,R.anim.slide_in_left)
        holder.itemView.startAnimation(animation)
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val imgNews: ImageView = itemView.findViewById(R.id.NewsImg)

        fun bind(itemNews: ItemNews) {

            tvTitle.text = itemNews.title
            if (!itemNews.imageUrl.isNullOrEmpty()) {
                Glide.with(itemView.context)
                    .load(itemNews.imageUrl)
                    .placeholder(R.drawable.ic_place_holder)
                    .centerCrop()
                    .error(R.drawable.ic_place_holder)
                    .into(imgNews)

            } else {
                imgNews.setImageDrawable(
                    ContextCompat.getDrawable(
                        itemView.context,
                        R.drawable.ic_place_holder
                    )
                )
            }
            itemView.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(itemNews.url)
                }
                itemView.context.startActivity(intent)
            }

        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ItemNews>() {
        override fun areItemsTheSame(oldItem: ItemNews, newItem: ItemNews): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: ItemNews, newItem: ItemNews): Boolean {
            return oldItem == newItem
        }
    }


}
