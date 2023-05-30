package com.cmchat.ui.main.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cmchat.cmchat.R
import com.cmchat.cmchat.databinding.FragmentHomeBinding
import com.cmchat.ui.main.home.adapters.FriendsAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val adapter by lazy {
        FriendsAdapter()
    }

    private val viewModel by viewModel<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.userList.adapter = adapter

        friendsObserver()

        viewModel.getFriends()

        itemClickListener()
    }

    private fun itemClickListener() {
        adapter.friendClick = {
            val bundle = bundleOf("id" to it)
            findNavController().navigate(
                R.id.action_HomeFragment_to_ChatFragment, bundle)
        }
    }

    private fun friendsObserver() {
        viewModel.friendsResponse.observe(viewLifecycleOwner) {
            it?.let {
                adapter.update(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}