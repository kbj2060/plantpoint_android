package com.example.plantpoint.ui.account

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.plantpoint.MainActivity
import com.example.plantpoint.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class AccountFragment : Fragment() {

    private lateinit var accountViewModel: AccountViewModel
    private var root: View? = null;
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val navHostFragment = (activity as MainActivity).supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        val backStackEntryCount = navHostFragment?.childFragmentManager?.fragments

        accountViewModel =
                ViewModelProvider(this).get(AccountViewModel::class.java)
        root = inflater.inflate(R.layout.fragment_account, container, false)
        auth = Firebase.auth
        val logout: Button = root!!.findViewById(R.id.logoutButton)
        logout.setOnClickListener {
            auth.signOut()
        }

        return root
    }
}