package com.cmchat.ui.main

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.cmchat.ImageHandler
import com.cmchat.application.Application
import com.cmchat.cmchat.databinding.FragmentEditProfileBinding
import java.text.SimpleDateFormat
import java.util.Locale

class EditProfileFragment : Fragment() {

    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>

    private var _binding : FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private var imgClicked = 0

    private val application by lazy {
        requireActivity().applicationContext as Application
    }
    private var backgroundImage : ByteArray? = null
    private var profileImage : ByteArray? = null
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
        backgroundImage = user.backgroundImage
        profileImage = user.profileImage
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        populateBackground()

        populateProfileImage()

        imagePickerLauncher = activityResultLauncher()

        binding.editProfileBackgroundImage.setOnClickListener {
            selectImage()
            imgClicked = 1
        }

        binding.editProfileImage.setOnClickListener {
            selectImage()
            imgClicked = 2
        }


        binding.editProfileNickname.setText(user.username)
        binding.editProfileUsername.setText(user.username)
        binding.editProfileBio.setText(user.bio)
        binding.editProfileBirthdate.setText(formatter.format(user.birthday!!))

    }

    private fun activityResultLauncher() =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                val data: Intent? = result.data
                val selectedImageUri: Uri? = data?.data
                val byteArray: ByteArray? = selectedImageUri?.let {
                    ImageHandler.uriToByteArray(
                        selectedImageUri,
                        requireActivity()
                    )
                }

                if (byteArray != null) {
                    if (imgClicked == 1) {
                        backgroundImage = byteArray
                        populateBackground()
                    } else if (imgClicked == 2) {
                        profileImage = byteArray
                        populateProfileImage()
                    }
                }
            }
        }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(intent)
    }

    private fun populateBackground(){
        if (backgroundImage != null) {
            Glide.with(requireContext()).load(backgroundImage).into(binding.editProfileBackgroundImage)
        }
    }
    private fun populateProfileImage(){
        if (profileImage != null) {
            Glide.with(requireContext()).load(profileImage).into(binding.editProfileImage)
        }
    }

}