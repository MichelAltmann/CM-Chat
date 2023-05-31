package com.cmchat.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cmchat.application.Application
import com.cmchat.cmchat.databinding.FragmentEditProfileBinding
import java.text.SimpleDateFormat
import java.util.Locale

class EditProfileFragment : Fragment() {

    private var _binding : FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private val application by lazy {
        requireActivity().applicationContext as Application
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = application.getUser()

        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        binding.editProfileNickname.setText(user.username)
        binding.editProfileUsername.setText(user.username)
        binding.editProfileBio.setText(user.bio)
        binding.editProfileBirthdate.setText(formatter.format(user.birthday!!))

    }

}