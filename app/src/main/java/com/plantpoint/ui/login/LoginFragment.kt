package com.plantpoint.ui.login

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.plantpoint.LoginActivity
import com.plantpoint.MainActivity

import com.google.firebase.auth.FirebaseAuth
import com.plantpoint.R


class LoginFragment : Fragment() {

  private var root: View? = null;
  private var mAuth: FirebaseAuth? = null
  var email: EditText? = null
  var password: EditText? = null
  var submit: Button? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    root = inflater.inflate(R.layout.fragment_login, container, false)

    this.mAuth = FirebaseAuth.getInstance()
    this.email = root!!.findViewById(R.id.email)
    this.password = root!!.findViewById(R.id.pwd)
    this.submit = root!!.findViewById(R.id.signInButton)
    val signup:Button = root!!.findViewById(R.id.signUp)
    val backButton: ImageButton = root!!.findViewById(R.id.back_button_from_login)

    backButton.setOnClickListener {
      (activity as LoginActivity).finish()
    }

    this.submit!!.setOnClickListener {
      if(notEmptyStringCheck(this.email!!.text.toString()) && notEmptyStringCheck(this.password!!.text.toString())){
        this.mAuth!!.signInWithEmailAndPassword(
          this.email!!.text.toString().trim(),
          this.password!!.text.toString().trim()
        ).addOnCompleteListener(activity as LoginActivity) { task ->
          if (task.isSuccessful) {
            Log.d("dd", "success")
            Intent(activity as LoginActivity, MainActivity::class.java).also {
              startActivity(it)
            }/*
            (activity as LoginActivity).finish()
*/
          } else {
            showLoginFailedPopup(inflater)
          }
        }
      }
    }

    signup.setOnClickListener {
      (activity as LoginActivity).supportFragmentManager
        .beginTransaction()
        .replace(R.id.login_fragment, SignUpFragment())
        .addToBackStack(null)
        .commit()
    }

    return root
  }

  @SuppressLint("ResourceAsColor")
  private fun showLoginFailedPopup(inflater : LayoutInflater) {
    val view = inflater.inflate(R.layout.login_popup, null)
    val textView: TextView = view.findViewById(R.id.popup_content)
    textView.text = "이메일 혹은 비밀번호를 확인해주세요."
    textView.gravity = Gravity.CENTER;
    val alertDialog = AlertDialog.Builder(activity as LoginActivity)
      .setPositiveButton("확인", null)
      .create()
    alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    alertDialog.setView(view)
    alertDialog.show()
  }

  fun notEmptyStringCheck(info : String) : Boolean{
    return (info.isNotEmpty() && info != "null")
  }
}