package com.plantpoint.ui.login

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.plantpoint.dto.User
import com.plantpoint.LoginActivity
import com.plantpoint.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpFragment : Fragment() {
    private lateinit var mAuth: FirebaseAuth
    private var db = FirebaseFirestore.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root =  inflater.inflate(R.layout.fragment_sign_up, container, false)

        this.mAuth = FirebaseAuth.getInstance()

        val email: EditText = root.findViewById(R.id.id_edit)
        val password: EditText = root.findViewById(R.id.pw_edit)
        val address: EditText = root.findViewById(R.id.address_edit)
        val name: EditText = root.findViewById(R.id.name_edit)
        val type_rg: RadioGroup = root.findViewById(R.id.type_rg)
        var type: String = ""
        val reg_front: EditText = root.findViewById(R.id.reg_front)
        val reg_back: EditText = root.findViewById(R.id.reg_back)
        val saveButton: Button = root.findViewById(R.id.save_user)
        val cancelButton : Button = root.findViewById(R.id.cancel_signup)

        type_rg.setOnCheckedChangeListener{ _, checkedId ->
            when (checkedId) {
                R.id.radio_consumer -> { type = "consumer" }
                R.id.radio_supplier -> { type = "supplier" }
            }
        }

        saveButton.setOnClickListener {
            if((activity as LoginActivity).notEmptyStringCheck(email.text.toString())
                    && (activity as LoginActivity).notEmptyStringCheck(password.text.toString())) {
                this.mAuth.createUserWithEmailAndPassword(email.text.toString().trim(), password.text.toString().trim())
                        .addOnCompleteListener((activity as LoginActivity), OnCompleteListener<AuthResult?> { task ->
                            if (task.isSuccessful) {
                                val user: User =
																	User(
																		type = type,
																		email = email.text.toString(),
																		address = address.text.toString(),
																		name = name.text.toString(),
																		registration = reg_front.text.toString() + reg_back.text.toString(),
																		uid = task.result!!.user!!.uid
																	)
                                saveUser(user)
                                goBackToLogin()
                            } else {
                                val error = task.exception.toString()
                                Log.d("llewyn", error)
                                when (error) {
                                    "ERROR_EMAIL_ALREADY_IN_USE" -> showSignUpFailedPopup(inflater)
                                }
                            }
                        })


            }
        }

        cancelButton.setOnClickListener {
            goBackToLogin()
        }

        return root
    }

    fun saveUser(user : User){
        db.collection("user").document()
                .set(user)
                .addOnSuccessListener { Log.d("TAG", "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }
    }

    private fun goBackToLogin(){
        (activity as LoginActivity).supportFragmentManager
                .popBackStack()
    }

    @SuppressLint("ResourceAsColor")
    private fun showSignUpFailedPopup(inflater : LayoutInflater) {
        val view = inflater.inflate(R.layout.login_popup, null)
        val textView: TextView = view.findViewById(R.id.popup_content)
        textView.text = "이미 등록된 이메일입니다."
        textView.gravity = Gravity.CENTER;
        val alertDialog = AlertDialog.Builder(activity as LoginActivity)
                .setPositiveButton("확인", null)
                .create()
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog.setView(view)
        alertDialog.show()
    }
}