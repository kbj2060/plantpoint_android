package com.example.plantpoint.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.example.plantpoint.LoginActivity
import com.example.plantpoint.MainActivity
import com.example.plantpoint.R
import com.google.firebase.auth.FirebaseAuth


class RequireLoginFragment : Fragment() {

    lateinit var root: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       /* val navHostFragment = (activity as MainActivity).supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        val backStackEntryCount = navHostFragment?.childFragmentManager?.fragments*/
        root = inflater.inflate(R.layout.fragment_require_login, container, false)
        val loginButton : Button = root.findViewById(R.id.login_button)
        val backButton : ImageButton = root.findViewById(R.id.back_button)
        val navController = Navigation.findNavController(activity as MainActivity, R.id.nav_host_fragment)

        backButton.setOnClickListener {
            navController.navigateUp()
        }

        loginButton.setOnClickListener{
            Intent(activity as MainActivity, LoginActivity::class.java).also {
                startActivity(it)
            }
        }
        return root
    }

    override fun onResume() {
        super.onResume()
        Log.d("dd", "RequireLogin")
        val user = FirebaseAuth.getInstance().currentUser
        if(user != null){
            val navController = (activity as MainActivity).findNavController(R.id.nav_host_fragment)
            navController.navigateUp()
        }
    }
}