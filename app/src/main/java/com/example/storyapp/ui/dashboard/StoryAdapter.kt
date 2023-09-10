package com.example.storyapp.ui.dashboard

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.databinding.StoryItemBinding
import com.example.storyapp.domain.model.Story
import com.example.storyapp.utils.Constant
import com.example.storyapp.utils.Helper

class StoryAdapter : ListAdapter<Story, StoryAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryAdapter.ViewHolder {
        val binding = StoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryAdapter.ViewHolder, position: Int) {
        val storyItem = getItem(position)
        holder.bind(storyItem)
    }

    class ViewHolder(private var binding: StoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(storyItem: Story) {
            binding.apply {
                ivStoryImage.layout(0, 0, 0, 0)
                Glide.with(itemView.context).load(storyItem.photoUrl).into(ivStoryImage)
                tvUsername.text = storyItem.name
                tvDate.text = Helper.convertDateTime(storyItem.createdAt)
                tvCaption.text = storyItem.description

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
                            storyItem
                        )
                    itemView.findNavController().navigate(toDetailFragment)
                }
            }
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<Story> = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(
                oldItem: Story, newItem: Story
            ): Boolean {
                return oldItem.id == newItem.id
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(
                oldItem: Story, newItem: Story
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}