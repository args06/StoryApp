package com.example.storyapp.ui.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.storyapp.databinding.FragmentDetailBinding
import com.example.storyapp.domain.model.Story
import com.example.storyapp.utils.Helper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var storyItem: Story

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        storyItem = DetailFragmentArgs.fromBundle(arguments as Bundle).storyItem

        binding.apply {
            tvUsernameTop.text = storyItem.name
            tvUsername.text = storyItem.name
            Glide.with(this@DetailFragment).load(storyItem.photoUrl).into(ivStoryImage)
            tvCaption.text = storyItem.description
            tvDate.text = Helper.convertDateTime(storyItem.createdAt)
        }
    }
}