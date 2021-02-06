package com.example.plantpoint.ui.neighborDetail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.example.plantpoint.dto.ChatRoom
import com.example.plantpoint.MainActivity
import com.example.plantpoint.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*


class NeighborDetailFragment : Fragment() {
  private lateinit var neighborDetailViewModel: NeighborDetailViewModel
  private lateinit var navController: NavController
  private lateinit var bundle: Bundle

  private val user = FirebaseAuth.getInstance().currentUser
  private val db = FirebaseFirestore.getInstance()
  private val roomRef = db.collection("room")

  override fun onCreateView(
          inflater: LayoutInflater,
          container: ViewGroup?,
          savedInstanceState: Bundle?
  ): View? {
    navController = Navigation.findNavController(activity as MainActivity, R.id.nav_host_fragment)
    neighborDetailViewModel = ViewModelProvider(this).get(NeighborDetailViewModel::class.java)

    val root = inflater.inflate(R.layout.fragment_neighbor_detail, container, false)

    val backButton: ImageView = root.findViewById(R.id.back_button)
    val farmerNameHolder:TextView = root.findViewById(R.id.farmer_name)
    val locationHolder:TextView = root.findViewById(R.id.farmer_location)
    val nbChat : FloatingActionButton = root.findViewById(R.id.chat_button)
    val farmerProfile: CircleImageView = root.findViewById(R.id.profile)

    val args = requireArguments()
    val nbUid = args.getString("uid").toString()
    val nbFarmerName = args.getString("farmerName").toString()
    val nbLocation = args.getString("farmerLocation").toString()
    val nbFarmerProfileURL = args.getString("farmerProfile").toString()

    farmerNameHolder.text = nbFarmerName
    locationHolder.text = nbLocation
    Glide.with(this).load(nbFarmerProfileURL).into(farmerProfile)

    backButton.setOnClickListener {
      moveBackToNeighbor()
    }

    nbChat.setOnClickListener {
      when {
        user === null -> {
          navController.navigate(R.id.action_neighbor_detail_to_navigation_require_login)
        }
        user.uid == nbUid -> {
          Log.d("dd" , "Cannot Chat With Same User")
        }
        else -> {
          handleChattingRoom(args)
        }
      }
    }

    return root
  }

  private fun moveBackToNeighbor() {
    navController.navigate(R.id.action_neighbor_detail_to_navigation_neighbor)
  }

  private fun addRoom(roomDocument: DocumentReference, chatRoom: ChatRoom) {
    roomDocument.set(chatRoom)
            .addOnSuccessListener { Log.d("TAG", "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }
  }

  private fun openChattingRoom(roomId: String){
    val args = requireArguments()
    bundle = bundleOf(
            "roomId" to roomId,
            "receiverUid" to args.getString("uid"),
            "receiverName" to args.getString("farmerName"),
            "directToChat" to true
    )
    navController.navigate(R.id.action_neighbor_detail_to_navigation_chat_room, bundle)
  }

  private fun handleChattingRoom(args:Bundle){
    val nbUid = args.getString("uid").toString()
    val nbFarmerName = args.getString("farmerName").toString()
    val nbFarmerProfileURL = args.getString("farmerProfile").toString()
    val me = (activity as MainActivity).me
    val meUid = me["uid"].toString()
    val validation: Map<String, Boolean> = mapOf(meUid to true , nbUid to true)
    val talkers =
            arrayListOf(
                        mapOf(
                              "uid" to me["uid"].toString(),
                              "name" to me["name"].toString(),
                              "profile" to me["profile"].toString()
                        ),
                        mapOf(
                              "uid" to nbUid,
                              "name" to nbFarmerName,
                              "profile" to nbFarmerProfileURL
                        )
            )
    talkers.sortBy{ it["uid"] }
    roomRef.whereEqualTo("validation.$meUid", true)
            .whereEqualTo("validation.$nbUid", true)
            .get()
            .addOnCompleteListener { task ->
              if (task.isSuccessful) {
                when (task.result!!.documents.size) {
                  0 -> {
                    val roomDocument = roomRef.document()
                    val newRoomId = roomDocument.id
                    val nChatRoom  = ChatRoom(newRoomId, "", validation, talkers, Date())
                    addRoom(roomDocument, nChatRoom)
                    openChattingRoom(newRoomId)
                  }
                  1 -> {
                    val existingRoomId = task.result!!.documents[0].id
                    openChattingRoom(existingRoomId)
                  }
                  else -> {
                    Log.e("dd", "Room Find Error")
                  }
                }
              } else {
                bundle = bundleOf()
                Log.d("TAG", "Error getting room check documents: ", task.exception)
              }
            }
  }
}