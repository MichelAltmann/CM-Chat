package com.cmchat.ui.popups

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.cmchat.cmchat.databinding.PopupAddFriendBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddFriendPopup : DialogFragment() {

    private var _binding : PopupAddFriendBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<AddFriendPopupViewModel>()

    companion object {
        const val TAG = "Add Friend Popup"
        fun newInstance() : AddFriendPopup {
            val fragment = AddFriendPopup()
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = PopupAddFriendBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addFriendResponseObserver()

        onAddClick()

        onCancelClick()

    }

    private fun onAddClick() {
        binding.popupAddFriendAddBtn.setOnClickListener {
            val username = binding.friendUsernameInput.text.toString()
            if (isBlank(username)) return@setOnClickListener
            viewModel.username = username
            viewModel.addFriend()
        }
    }

    private fun onCancelClick() {
        binding.popupAddFriendCancelBtn.setOnClickListener {
            dialog?.hide()
        }
    }

    private fun isBlank(username: String): Boolean {
        if (username.equals("")) {
            binding.friendUsernameInput.error = "Invalid Username."
            binding.friendUsernameInput.requestFocus()
            return true
        }
        if (username.equals(viewModel.getUser().username)){
            binding.friendUsernameInput.error = "Are you not your friend?"
            binding.friendUsernameInput.requestFocus()
            return true
        }
        return false
    }

    private fun addFriendResponseObserver() {
        viewModel.friendAddResponse.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            dialog?.hide()
        }
        viewModel.error.observe(viewLifecycleOwner) {
            binding.friendUsernameInput.error = it.message
            binding.friendUsernameInput.requestFocus()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

}