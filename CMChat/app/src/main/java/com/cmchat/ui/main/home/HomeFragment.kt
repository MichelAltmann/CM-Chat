package com.cmchat.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.cmchat.application.Application
import com.cmchat.cmchat.R
import com.cmchat.cmchat.databinding.FragmentHomeBinding
import com.cmchat.ui.main.home.adapters.FriendsAdapter
import com.cmchat.ui.popups.AddFriendPopup
import com.cmchat.util.ImageHandler
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val adapter by lazy {
        FriendsAdapter()
    }

    private val viewModel by viewModel<HomeViewModel>()

    private val application by lazy {
        requireActivity().applicationContext as Application
    }

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

        onFabClick()

        toolbarSetup()
    }

    private fun toolbarSetup() {
        updateUser()
    }

    private fun updateUser(){
        binding.profileCard.setOnClickListener {
            findNavController().navigate(R.id.action_HomeFragment_to_ProfileFragment)
        }
        Glide.with(requireContext()).load(ImageHandler.IMAGE_GETTER_URL+application.getUser().profileImage).placeholder(R.drawable.ic_user).into(binding.profileIcon)
    }

    private fun onFabClick() {
        binding.homeAddFriendFab.setOnClickListener {
            AddFriendPopup.newInstance().show(parentFragmentManager, AddFriendPopup.TAG)
        }
    }

    private fun itemClickListener() {
        adapter.friendClick = {
            val bundle = bundleOf("friend" to it)
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