package com.cmchat.ui.main

import androidx.lifecycle.ViewModelProvider
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
import java.text.SimpleDateFormat
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val application by lazy {
        requireActivity().applicationContext as Application
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

        val user = application.getUser()

        Glide.with(requireContext()).load(user.backgroundImage)
            .placeholder(R.drawable.enter_text_background).into(binding.profileBackgroundImage)

        Glide.with(requireContext()).load(user.profileImage).placeholder(R.drawable.ic_user)
            .into(binding.profileImage)

        val formatter = SimpleDateFormat("dd 'of' MMMM yyyy", Locale.getDefault())

        binding.profileNickname.text = user.username
        binding.profileBio.text = user.bio
        "Born in ${formatter.format(user.birthday!!)}".also { binding.profileBirthdate.text = it }

        binding.profileEditProfileBtn.setOnClickListener {
            findNavController().navigate(R.id.action_ProfileFragment_to_EditProfileFragment)
        }

    }
}