package com.cmchat.ui.main.chat

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import com.cmchat.application.Application
import com.cmchat.cmchat.databinding.FragmentChatBinding
import com.cmchat.model.Message
import com.cmchat.model.User
import com.google.gson.Gson
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null

    private val binding get() = _binding!!
    private val messagesAdapter by lazy {
        MessagesAdapter()
    }
    private val messages: ArrayList<Message> = arrayListOf()

    private val PICK_IMAGE_REQUEST = 1
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    private lateinit var myApplication: Application
    private lateinit var user: User
    private var id = 0
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

        imagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {

                    val data: Intent? = result.data
                    val selectedImageUri: Uri? = data?.data
                    val byteArray: ByteArray? =
                        selectedImageUri?.let { uriToByteArray(selectedImageUri) }

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
                                null,
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

        socket.on("newMessage" + user.id) { args ->
            if (args[0] != null) {
                val messageJson = args[0] as JSONObject
                val gson = Gson()
                val message = gson.fromJson(messageJson.toString(), Message::class.java)

                if (user.id == message.senderId) {
                    messages.removeLast()
                }
                requireActivity().runOnUiThread {
                    messages.add(message)
                    messagesAdapter.update(messages, user)
                    binding.messagesRecycler.scrollToPosition(messages.size - 1)
                }
            }
        }


    }


    private fun uriToByteArray(uri: Uri): ByteArray? {
        val contentResolver = requireActivity().contentResolver
        val inputStream = contentResolver.openInputStream(uri)

        val orientation = getExifOrientation(requireContext(), uri)

        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeStream(inputStream, null, options)
        inputStream?.close()

        val imageWidth = options.outWidth
        val imageHeight = options.outHeight

        // Calculate the sample size to reduce the image size while maintaining aspect ratio
        var sampleSize = 1
        while ((imageWidth / sampleSize) * (imageHeight / sampleSize) > 500000) {
            sampleSize *= 2
        }

        // Decode the image with the calculated sample size
        val decodeOptions = BitmapFactory.Options()
        decodeOptions.inSampleSize = sampleSize

        val compressedBitmap = BitmapFactory.decodeStream(
            contentResolver.openInputStream(uri),
            null,
            decodeOptions
        )

        val rotatedBitmap = rotateBitmap(compressedBitmap, orientation)

        val outputStream = ByteArrayOutputStream()
        rotatedBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

        return outputStream.toByteArray()
    }

    private fun getExifOrientation(applicationContext: Context, uri: Uri): Int {
        var orientation = ExifInterface.ORIENTATION_UNDEFINED
        try {
            val inputStream = applicationContext.contentResolver.openInputStream(uri)
            inputStream?.use {
                val exifInterface = ExifInterface(it)
                orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED
                )
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return orientation
    }

    fun rotateBitmap(bitmap: Bitmap?, orientation: Int): Bitmap? {
        if (bitmap == null) return null

        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
        }

        return Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.width,
            bitmap.height,
            matrix,
            true
        )
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

    override fun onDestroyView() {
        super.onDestroyView()
    }
}