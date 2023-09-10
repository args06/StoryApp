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
import com.example.storyapp.databinding.FragmentDashboardBinding
import com.example.storyapp.ui.adapter.LoadingStateAdapter
import com.example.storyapp.ui.adapter.StoryAdapter
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
            storyAdapter.submitData(lifecycle, storiesData)
        }

        storyAdapter.addLoadStateListener { loadState ->
            if (loadState.append.endOfPaginationReached)
                if (storyAdapter.itemCount < 1)
                    showNoData(true)
                else
                    showNoData(false)
        }

        binding.rvStoryList.apply {
            layoutManager = StaggeredGridLayoutManager(1, 1)
            adapter = storyAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    storyAdapter.retry()
                }
            )
            setHasFixedSize(true)
        }
    }

    private fun showNoData(isNoData: Boolean) {
        binding.ivNoData.visibility = if (isNoData) View.VISIBLE else View.GONE
        binding.rvStoryList.visibility = if (isNoData) View.GONE else View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}