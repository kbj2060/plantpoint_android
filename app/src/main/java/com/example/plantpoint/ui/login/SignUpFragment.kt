package com.example.plantpoint.ui.login

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.plantpoint.DTO.User
import com.example.plantpoint.LoginActivity
import com.example.plantpoint.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SignInFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SignUpFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var mAuth: FirebaseAuth
    private var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

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
                                val user: User = User(
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