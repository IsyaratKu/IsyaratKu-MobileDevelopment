package com.isyaratku.app.ui.main.leaderboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.isyaratku.app.api.UsersItem
import com.isyaratku.app.databinding.RankLayoutBinding

class RankRecyclerView (private val userList: List<UsersItem?>?) : RecyclerView.Adapter <RankRecyclerView.MyViewHolder>(){

    class MyViewHolder(val binding: RankLayoutBinding):
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            RankLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun getItemCount(): Int {
        return userList!!.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = userList!![position]
        holder.binding.apply {
            tvname.text = currentItem!!.username
            tvpoint.text = currentItem.score.toString()
            Glide.with(card)
                .load(currentItem.urlPhoto)
                .centerCrop()
                .into(storyImage)


        }

    }


}