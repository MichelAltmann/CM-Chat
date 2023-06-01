package com.cmchat.ui.main.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

    private val application by lazy {
        requireActivity().applicationContext as Application
    }

    private lateinit var user : User
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        friendsRequestObserver()

        viewModel.getFriends()

        onRefuseRequestClick()

        onAcceptRequestClick()

        editProfileClick()

        addFriendFabClick()

    }

    override fun onResume() {
        super.onResume()

        user = application.getUser()
        viewModel.user = user
        putBackgroundImage()

        putProfileImage()

        binding.profileNickname.text = user.nickname

        binding.profileUsername.text = user.username

        binding.profileBio.text = user.bio

        val formatter = SimpleDateFormat("dd 'of' MMMM yyyy", Locale.getDefault())

        "Born in ${formatter.format(user.birthday!!)}".also { binding.profileBirthdate.text = it }
    }

    private fun onAcceptRequestClick() {
        adapter.acceptRequestClick = {
            viewModel.acceptRequest(it)
        }
    }

    private fun onRefuseRequestClick() {
        adapter.refuseRequestClick = {
            viewModel.refuseRequest(it)
        }
    }

    private fun putProfileImage() {
        Glide.with(requireContext()).load(user.profileImage).placeholder(R.drawable.ic_user)
            .into(binding.profileImage)
    }

    private fun putBackgroundImage() {
        Glide.with(requireContext()).load(user.backgroundImage)
            .placeholder(R.drawable.enter_text_background).into(binding.profileBackgroundImage)
    }

    private fun friendsRequestObserver() {
        viewModel.friendsRequestResponse.observe(viewLifecycleOwner) {
            binding.profileFriendsRequestRecycler.adapter = adapter
            adapter.update(it)
        }
        viewModel.requestResponse.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            viewModel.getFriends()
        }
        viewModel.requestError.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
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