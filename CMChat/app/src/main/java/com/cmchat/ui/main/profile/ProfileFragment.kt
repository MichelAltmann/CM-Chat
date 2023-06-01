package com.cmchat.ui.main.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.cmchat.application.Application
import com.cmchat.cmchat.R
import com.cmchat.cmchat.databinding.FragmentProfileBinding
import com.cmchat.model.User
import com.cmchat.ui.popups.AddFriendPopup
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<ProfileViewModel>()

    private val adapter by lazy {
        FriendsRequestAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = viewModel.getUser()

        putBackgroundImage(user)

        putProfileImage(user)

        friendsRequestObserver()

        viewModel.getFriends()

        binding.profileNickname.text = user.nickname

        binding.profileUsername.text = user.username

        binding.profileBio.text = user.bio

        val formatter = SimpleDateFormat("dd 'of' MMMM yyyy", Locale.getDefault())

        "Born in ${formatter.format(user.birthday!!)}".also { binding.profileBirthdate.text = it }

        editProfileClick()

        addFriendFabClick()

    }

    private fun putProfileImage(user: User) {
        Glide.with(requireContext()).load(user.profileImage).placeholder(R.drawable.ic_user)
            .into(binding.profileImage)
    }

    private fun putBackgroundImage(user: User) {
        Glide.with(requireContext()).load(user.backgroundImage)
            .placeholder(R.drawable.enter_text_background).into(binding.profileBackgroundImage)
    }

    private fun friendsRequestObserver() {
        viewModel.friendsRequestResponse.observe(viewLifecycleOwner) {
            binding.profileFriendsRequestRecycler.adapter = adapter
            adapter.update(it)
        }
    }

    private fun editProfileClick() {
        binding.profileEditProfileBtn.setOnClickListener {
            findNavController().navigate(R.id.action_ProfileFragment_to_EditProfileFragment)
        }
    }

    private fun addFriendFabClick() {
        binding.profileAddFriendFab.setOnClickListener {
            AddFriendPopup.newInstance().show(parentFragmentManager, AddFriendPopup.TAG)
        }
    }
}