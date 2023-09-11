package com.example.storyapp.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.storyapp.data.local.entity.StoryEntity
import com.example.storyapp.databinding.FragmentDetailBinding
import com.example.storyapp.utils.Helper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var storyEntityItem: StoryEntity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        storyEntityItem = DetailFragmentArgs.fromBundle(arguments as Bundle).storyItem

        binding.apply {
            tvUsernameTop.text = storyEntityItem.name
            tvUsername.text = storyEntityItem.name
            Glide.with(this@DetailFragment).load(storyEntityItem.photoUrl).into(ivStoryImage)
            tvCaption.text = storyEntityItem.description
            tvDate.text = Helper.convertDateTime(storyEntityItem.createdAt)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}