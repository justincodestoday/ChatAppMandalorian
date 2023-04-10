package com.mandalorian.chatapp.ui.presentation.message

import com.mandalorian.chatapp.CustomEditText.KeyBoardInputCallbackListener
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.core.view.inputmethod.InputConnectionCompat
import androidx.core.view.inputmethod.InputContentInfoCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.mandalorian.chatapp.CustomEditText
import com.mandalorian.chatapp.R
import com.mandalorian.chatapp.databinding.FragmentMessageBinding
import com.mandalorian.chatapp.ui.presentation.adapters.MessageAdapter
import com.mandalorian.chatapp.ui.presentation.base.BaseFragment
import com.mandalorian.chatapp.ui.presentation.message.viewModel.MessageViewModel
import com.mandalorian.chatapp.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class MessageFragment : BaseFragment<FragmentMessageBinding>() {
    private lateinit var adapter: MessageAdapter
    override val viewModel: MessageViewModel by viewModels()
    override fun getLayoutResource() = R.layout.fragment_message
    private val args: MessageFragmentArgs by navArgs()

    private val typingHandler = Handler(Looper.getMainLooper())
    private val typingDelay = 3000L // 3 seconds delay
    private val typingRunnable = Runnable {
        viewModel.setTypingIndicator(false)
    }

    private fun onMessageTextChanged() {
        val messageText = binding?.etMessage?.text.toString()
        if (messageText.isBlank()) {
            typingHandler.removeCallbacks(typingRunnable)
            viewModel.setTypingIndicator(false)
        } else {
            typingHandler.removeCallbacks(typingRunnable)
            viewModel.setTypingIndicator(true)
            typingHandler.postDelayed(typingRunnable, typingDelay)
        }
    }

    override fun onBindView(view: View, savedInstanceState: Bundle?) {
        super.onBindView(view, savedInstanceState)
        binding?.viewModel = viewModel
        setupAdapter()

        Log.d("UID", args.id)

        binding?.etMessage?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // not needed in this case
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // call onMessageTextChanged to update the typing indicator
                onMessageTextChanged()
            }

            override fun afterTextChanged(s: Editable?) {
                // not needed in this case
            }
        })

        binding?.run {
            viewModel?.getUser(args.id)
            viewModel?.person?.observe(viewLifecycleOwner) { person ->
                tvUsername.text = person?.username ?: ""
            }

            viewModel?.user?.observe(viewLifecycleOwner) { user ->
                val lastSeen = user.lastSeen
                val date = Date(lastSeen)
                val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
                val lastSeenTime = formatter.format(date)
                if (user?.isTyping == true) {
                    tvOnlineStatus.text = "typing..."
                } else if (user?.online == true) {
                    tvOnlineStatus.text = "Online ●"
                } else {
                    tvOnlineStatus.text = "Last seen: $lastSeenTime"
                }
            }
        }
    }


    override fun onBindData(view: View) {
        super.onBindData(view)

        viewModel.initializeUserStatus()

        viewModel.messages.asLiveData().observe(viewLifecycleOwner) {
            adapter.setMessages(it.toMutableList())
            binding?.rvMessages?.scrollToPosition(adapter.itemCount - 1)
        }
    }

    fun setupAdapter() {
        val layoutManager = LinearLayoutManager(requireContext())
        adapter = MessageAdapter(mutableListOf(), requireContext())

        binding?.rvMessages?.adapter = adapter
        binding?.rvMessages?.layoutManager = layoutManager
    }
}