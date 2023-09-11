package com.example.storyapp.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.data.local.entity.StoryEntity
import com.example.storyapp.databinding.StoryItemBinding
import com.example.storyapp.ui.dashboard.DashboardFragmentDirections
import com.example.storyapp.utils.Constant
import com.example.storyapp.utils.Helper

class StoryAdapter : PagingDataAdapter<StoryEntity, StoryAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = StoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val storyItem = getItem(position)
        if (storyItem != null) {
            holder.bind(storyItem)
        }
    }

    class ViewHolder(private var binding: StoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(storyEntityItem: StoryEntity) {
            binding.apply {
                ivStoryImage.layout(0, 0, 0, 0)
                Glide.with(itemView.context).load(storyEntityItem.photoUrl).into(ivStoryImage)
                tvUsername.text = storyEntityItem.name
                tvDate.text = Helper.convertDateTime(storyEntityItem.createdAt)
                tvCaption.text = storyEntityItem.description

                if (storyEntityItem.lat != null && storyEntityItem.lon != null) {
                    tvCountry.text = Helper.getCityName(
                        itemView.context, storyEntityItem.lat, storyEntityItem.lon
                    )
                }

                tvCaption.post {
                    val lineCount = tvCaption.lineCount
                    if (lineCount < Constant.CAPTION_MAX_LINE) {
                        tvShowMore.visibility = View.GONE;
                    } else {
                        tvCaption.maxLines = Constant.CAPTION_MAX_LINE
                        tvShowMore.visibility = View.VISIBLE
                    }
                }

                tvShowMore.setOnClickListener {
                    if (tvShowMore.text.toString() == itemView.context.getString(R.string.show_more)) {
                        tvCaption.maxLines = Int.MAX_VALUE
                        tvShowMore.text = itemView.context.getString(R.string.show_less)
                    } else {
                        tvCaption.maxLines = Constant.CAPTION_MAX_LINE
                        tvShowMore.text = itemView.context.getString(R.string.show_more)
                    }
                }

                cvStoryItem.setOnClickListener {
                    val toDetailFragment =
                        DashboardFragmentDirections.actionDashboardFragmentToDetailFragment(
                            storyEntityItem
                        )
                    itemView.findNavController().navigate(toDetailFragment)
                }
            }
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<StoryEntity> =
            object : DiffUtil.ItemCallback<StoryEntity>() {
                override fun areItemsTheSame(
                    oldItem: StoryEntity, newItem: StoryEntity
                ): Boolean {
                    return oldItem.id == newItem.id
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(
                    oldItem: StoryEntity, newItem: StoryEntity
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}