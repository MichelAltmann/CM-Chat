package com.cmchat.ui.main.profile.editprofile

import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.cmchat.application.Application
import com.cmchat.cmchat.databinding.FragmentEditProfileBinding
import com.cmchat.model.User
import com.cmchat.util.DatePickerFragment
import com.cmchat.util.ImageHandler
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Locale


class EditProfileFragment : Fragment(), DatePickerDialog.OnDateSetListener {

    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private var imgClicked = 0

    private val application by lazy {
        requireActivity().applicationContext as Application
    }
    private val user by lazy {
        application.getUser()
    }
    private var backgroundImage: String? = null
    private var profileImage: String? = null
    private val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private var calendar = Calendar.getInstance()

    private val viewModel by viewModel<EditProfileViewModel>()

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

        backgroundImage = user.backgroundImage
        profileImage = user.profileImage

        val editText: EditText = binding.editProfileBio
        val maxLines = 5
        editText.maxLines = maxLines
        val filters = arrayOf<InputFilter>(InputFilter { source, _, _, _, _, _ ->
            val newLineCount = (source ?: "").count { it == '\n' } + editText.lineCount
            if (newLineCount <= maxLines) null else ""
        })
        editText.filters = filters

        populateBackground()

        populateProfileImage()

        imagePickerLauncher = activityResultLauncher()

        backgroundImageClick()

        profileImageClick()

        calendarFabClick()

        userEditObserver()

        onBirthdateText()

        val nicknameEt = binding.editProfileNickname
        val usernameEt = binding.editProfileUsername
        val bioEt = binding.editProfileBio
        val birthdateEt = binding.editProfileBirthdate
        nicknameEt.setText(user.nickname)
        usernameEt.setText(user.username)
        bioEt.setText(user.bio)
        birthdateEt.setText(formatter.format(user.birthday!!))

        doneBtnClick(nicknameEt, usernameEt, bioEt, birthdateEt, user)

        binding.editProfileEditCancelBtn.setOnClickListener {
            findNavController().navigateUp()
            if (user.profileImage != profileImage) {
                viewModel.deleteImage(profileImage)
            }
            if (user.backgroundImage != backgroundImage) {
                viewModel.deleteImage(backgroundImage)
            }
        }

    }

    private fun doneBtnClick(
        nicknameEt: EditText,
        usernameEt: EditText,
        bioEt: EditText,
        birthdateEt: EditText,
        user: User
    ) {
        binding.editProfileEditDoneBtn.setOnClickListener {
            val nickname = nicknameEt.text.toString()
            val username = usernameEt.text.toString()
            val bio = bioEt.text.toString()
            val birthdate = birthdateEt.text.toString()
            if (invalidNickname(nickname, nicknameEt)) return@setOnClickListener

            if (invalidUsername(username, usernameEt)) return@setOnClickListener

            if (invalidBirthDate(birthdate, birthdateEt)) return@setOnClickListener


            val date = formatter.parse(birthdateEt.text.toString())

            if (user.profileImage != profileImage) {
                viewModel.deleteImage(user.profileImage)
            }

            if (user.backgroundImage != backgroundImage) {
                viewModel.deleteImage(user.backgroundImage)
            }

            val updatedUser = User(
                user.id,
                user.email,
                nickname,
                username,
                null,
                date,
                profileImage,
                backgroundImage,
                bio,
                null,
                0,
                0
            )

            viewModel.edit(updatedUser)
        }
    }

    private fun userEditObserver() {

        viewModel.userResponse.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), "Profile edited Successfully!", Toast.LENGTH_SHORT)
                .show()
            findNavController().navigateUp()
        }

        viewModel.error.observe(viewLifecycleOwner) {
            if (it.message == "Username already in use") {
                binding.editProfileUsername.error = it.message
                binding.editProfileUsername.requestFocus()
            }
        }

        viewModel.bgResponse.observe(viewLifecycleOwner) {
            backgroundImage = it.imageId
            Log.i(TAG, "userEditObserver: ")
            populateBackground()
        }

        viewModel.profileResponse.observe(viewLifecycleOwner) {
            profileImage = it.imageId
            populateProfileImage()
        }
    }

    private fun invalidBirthDate(birthdate: String, birthdateEt: EditText): Boolean {
        if (birthdate == "") {
            birthdateEt.error = "Invalid date."
            birthdateEt.requestFocus()
            return true
        }
        return false
    }

    private fun invalidUsername(username: String, usernameEt: EditText): Boolean {
        if (username == "") {
            usernameEt.error = "Invalid Username."
            usernameEt.requestFocus()
            return true
        }
        return false
    }

    private fun invalidNickname(nickname: String, nicknameEt: EditText): Boolean {
        if (nickname == "") {
            nicknameEt.error = "Invalid Nickname."
            nicknameEt.requestFocus()
            return true
        }
        return false
    }

    private fun calendarFabClick() {
        binding.editProfileCalendarFab.setOnClickListener {
            val datePicker = DatePickerFragment(this)
            datePicker.show(parentFragmentManager, "Date dialog")
        }
    }

    private fun profileImageClick() {
        binding.editProfileImage.setOnClickListener {
            selectImage()
            imgClicked = 2
        }
    }

    private fun backgroundImageClick() {
        binding.editProfileBackgroundImage.setOnClickListener {
            selectImage()
            imgClicked = 1
        }
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
                    if (imgClicked == 1) { // 1 == background
                        viewModel.uploadImage(byteArray, 1)
                    } else if (imgClicked == 2) { // 2 == profile
                        viewModel.uploadImage(byteArray, 2)
                    }
                }
            }
        }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(intent)
    }

    private fun populateBackground() {
        if (backgroundImage != null) {
            Glide.with(requireContext()).load(ImageHandler.IMAGE_GETTER_URL + backgroundImage)
                .into(binding.editProfileBackgroundImage)
        }
    }

    private fun populateProfileImage() {
        if (profileImage != null) {
            Glide.with(requireContext()).load(ImageHandler.IMAGE_GETTER_URL + profileImage)
                .into(binding.editProfileImage)
        }
    }

    private fun updateBirthdate() {
        binding.editProfileBirthdate.setText(formatter.format(calendar.time))
    }

    private fun onBirthdateText() {
        binding.editProfileBirthdate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

            override fun afterTextChanged(s: Editable?) {
                var text = s.toString()
                if (text.isNotEmpty() && text.last() != '/') {
                    val formattedText = StringBuilder(text)
                    when (text.length) {
                        3, 6 -> {
                            formattedText.insert(text.length - 1, "/")
                            binding.editProfileBirthdate.setText(formattedText.toString())
                            binding.editProfileBirthdate.setSelection(formattedText.length)
                        }
                    }
                }
            }

        })
    }

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, day: Int) {
        calendar = GregorianCalendar(year, month, day)
        updateBirthdate()
    }

}