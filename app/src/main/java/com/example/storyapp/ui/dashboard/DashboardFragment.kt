package com.example.storyapp.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.storyapp.R
import com.example.storyapp.data.Results
import com.example.storyapp.databinding.FragmentDashboardBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DashboardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getSessionData()

        binding.fabInsert.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_insertFragment)
        }
    }

    private fun getSessionData() {
        viewModel.getUserSessionData().observe(viewLifecycleOwner) { userData ->
            if (userData != null) getStories(userData.token)
        }
    }

    private fun getStories(token: String) {
        val storyAdapter = StoryAdapter()
        viewModel.getStories(token).observe(viewLifecycleOwner) { storiesData ->
            if (storiesData != null) {
                when (storiesData) {
                    is Results.Loading -> {
                        showLoading(true)
                    }

                    is Results.Success -> {
                        showLoading(false)
                        val data = storiesData.data
                        if (data.isNotEmpty()) {
                            storyAdapter.submitList(data)
                        } else {
                            showNoData()
                        }
                    }

                    is Results.Error -> {
                        showLoading(false)
                        showSnackBar(storiesData.error)
                    }
                }
            }
        }

        binding.rvStoryList.apply {
            layoutManager = StaggeredGridLayoutManager(1, 1)
            adapter = storyAdapter
            setHasFixedSize(true)
        }
    }

    private fun showNoData() {
        binding.ivNoData.visibility = View.VISIBLE
        binding.ivLoading.visibility = View.GONE
        binding.rvStoryList.visibility = View.GONE
    }

    private fun showLoading(isLoading: Boolean) {
        binding.ivLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.ivNoData.visibility = View.GONE
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.constraintLayout, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}