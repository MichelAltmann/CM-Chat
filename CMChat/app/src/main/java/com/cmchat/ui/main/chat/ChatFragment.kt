package com.cmchat.ui.main.chat

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.cmchat.util.ImageHandler
import com.cmchat.application.Application
import com.cmchat.cmchat.databinding.FragmentChatBinding
import com.cmchat.model.Message
import com.cmchat.model.User
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null

    private val binding get() = _binding!!
    private val messagesAdapter by lazy {
        MessagesAdapter()
    }
    private val messages: ArrayList<Message> = arrayListOf()

    var visibility = false

    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    private lateinit var myApplication: Application
    private lateinit var user: User
    private var id = 0
    private val viewModel by viewModel<ChatViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myApplication = requireActivity().applicationContext as Application

        val user = myApplication.getUser()

        id = arguments?.getInt("id")!!

        Log.i(ContentValues.TAG, "onCreate: " + id + " " + user.id)

        val socket = myApplication.getSocket()

        viewModel.messageResponse.observe(viewLifecycleOwner) {
            if (user.id == it.senderId) messages.removeLast()
            messages.add(it)
            messagesAdapter.update(messages, user)
            binding.messagesRecycler.scrollToPosition(messages.size - 1)
        }

        viewModel.newMessage()

        imagePickerLauncher =
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
                        val messageJson = createJsonMessage(
                            user,
                            binding.textInput.text.toString(),
                            id,
                            byteArray,
                            status = "sending"
                        )
                        messages.add(
                            Message(
                                user.id,
                                id,
                                binding.textInput.text.toString(),
                                byteArray,
                                "sending"
                            )
                        )
                        messagesAdapter.update(messages, user)
                        socket.emit("newMessage", messageJson)
                    }
                }
            }


        binding.messagesRecycler.adapter = messagesAdapter

        binding.textInputLayout.setEndIconOnClickListener {
            selectImage()
        }

        binding.fabSendMessage.setOnClickListener {
            if (binding.textInput.text.toString().isNotEmpty()) {

                val messageJson = createJsonMessage(
                    user,
                    binding.textInput.text.toString(),
                    id,
                    null,
                    status = "sending"
                )
                messages.add(
                    Message(
                        user.id,
                        id,
                        binding.textInput.text.toString(),
                        null,
                        "sending"
                    )
                )
                messagesAdapter.update(messages, user)
                socket.emit("newMessage", messageJson)
                binding.textInput.setText("")

            }
        }


    }

    private fun createJsonMessage(
        user: User,
        text: String,
        id: Int,
        image: ByteArray?,
        status: String
    ): String {
        val message = Message(user.id, id, text, image, status)
        val gson = Gson()
        val messageJson = gson.toJson(message)
        return messageJson
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(intent)
    }

    override fun onStart() {
        super.onStart()
        visibility = true
    }

    override fun onStop() {
        super.onStop()
        visibility = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}