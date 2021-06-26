package com.example.jymycabdriverapp.ui.notifications

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.jymycabdriverapp.Activity.*
import com.example.jymycabdriverapp.MainActivity
import com.example.jymycabdriverapp.R
import com.example.jymycabdriverapp.databinding.FragmentNotificationsBinding

class NotificationsFragment : Fragment() {

    private lateinit var notificationsViewModel: NotificationsViewModel
    private var _binding: FragmentNotificationsBinding? = null
    lateinit var ll_Profile:LinearLayout
    lateinit var ll_Notification:LinearLayout
    lateinit var ll_wallet:LinearLayout
    lateinit var ll_history:LinearLayout
    lateinit var ll_termsandcondition:LinearLayout

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        ll_Profile = root.findViewById(R.id.ll_Profile)
        ll_Notification = root.findViewById(R.id.ll_Notification)
        ll_wallet = root.findViewById(R.id.ll_wallet)
        ll_history = root.findViewById(R.id.ll_history)
        ll_termsandcondition = root.findViewById(R.id.ll_termsandcondition)
        ll_Profile.setOnClickListener {
            val intent = Intent(requireContext(), ProfileActivity::class.java)
            startActivity(intent)
        }
        ll_wallet.setOnClickListener {
            val intent = Intent(requireContext(), Wallet::class.java)
            startActivity(intent)
        }
        ll_termsandcondition.setOnClickListener {
            val intent = Intent(requireContext(), Terms_And_Condition::class.java)
            startActivity(intent)
        }
        ll_history.setOnClickListener {
            val intent = Intent(requireContext(), History::class.java)
            startActivity(intent)
        }
        ll_Notification.setOnClickListener {
            val intent = Intent(requireContext(), Notification::class.java)
            startActivity(intent)
        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}