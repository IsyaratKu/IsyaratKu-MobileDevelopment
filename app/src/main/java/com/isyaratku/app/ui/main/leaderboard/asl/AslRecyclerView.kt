package com.isyaratku.app.ui.main.leaderboard.asl

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.isyaratku.app.R
import com.isyaratku.app.api.UsersItem
import com.isyaratku.app.databinding.RankLayoutBinding

class AslRecyclerView (private val userList: List<UsersItem?>?) : RecyclerView.Adapter <AslRecyclerView.MyViewHolder>(){

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
        val animation = AnimationUtils.loadAnimation(holder.itemView.context,R.anim.slide_in_left)
        holder.itemView.startAnimation(animation)
        val currentItem = userList!![position]
        holder.binding.apply {
            tvname.text = currentItem!!.username
            if (currentItem.aslScore == null) {
                tvpoint.text = "0"
            } else {
                tvpoint.text = currentItem.aslScore.toString()
            }
                Glide.with(card)
                    .load(currentItem.urlPhoto)
                    .centerCrop()
                    .error(R.drawable.baseline_person_24)
                    .into(storyImage)




        }

    }


}