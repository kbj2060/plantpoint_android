package com.example.plantpoint.ui.account

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.plantpoint.LoginActivity
import com.example.plantpoint.MainActivity
import com.example.plantpoint.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class AccountFragment : Fragment() {

    private lateinit var accountViewModel: AccountViewModel
    private var root: View? = null;
    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        accountViewModel =
                ViewModelProvider(this).get(AccountViewModel::class.java)
        root = inflater.inflate(R.layout.fragment_account, container, false)
        auth = Firebase.auth

        val navController = Navigation.findNavController(activity as MainActivity, R.id.nav_host_fragment)
        var me = (activity as MainActivity).me
        val logout: LinearLayout = root!!.findViewById(R.id.logout)
        val nameText: TextView = root!!.findViewById(R.id.name_text)
        val profile: ImageView = root!!.findViewById(R.id.profile)

        Glide.with(activity as MainActivity)
              .load(me["profile"])
              .transform(CenterInside(), RoundedCorners(10))
              .into(profile)

        nameText.text = (activity as MainActivity).me["name"].toString()
        logout.setOnClickListener {
          auth.signOut()
          Intent(activity as MainActivity, LoginActivity::class.java).also {
            startActivity(it)
          }
        }

        return root
    }

    override fun onStart(){
      super.onStart()
      val nameText: TextView = root!!.findViewById(R.id.name_text)
      Log.d("dd", user.toString())
      nameText.text = (activity as MainActivity).me["name"].toString()
    }

  fun closeKeyboard()
  {
    val mInputMethodManager =
      requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    mInputMethodManager.hideSoftInputFromWindow(requireView().windowToken, 0)
  }
}