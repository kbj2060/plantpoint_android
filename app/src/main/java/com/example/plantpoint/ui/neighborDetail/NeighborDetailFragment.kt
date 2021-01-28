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
import com.example.plantpoint.dto.ChatRoom
import com.example.plantpoint.MainActivity
import com.example.plantpoint.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*


class NeighborDetailFragment : Fragment() {
    private lateinit var neighborDetailViewModel: NeighborDetailViewModel
    private lateinit var navController: NavController
    private lateinit var bundle: Bundle

    private val user = FirebaseAuth.getInstance().currentUser
    private val db = FirebaseFirestore.getInstance()

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

        val nbUid = requireArguments().getString("uid").toString()
        val nbFarmerName = requireArguments().getString("farmerName").toString()
        val nbLocation = requireArguments().getString("farmerLocation").toString()

        farmerNameHolder.text = nbFarmerName
        locationHolder.text = nbLocation

        backButton.setOnClickListener {
            moveBackToNeighbor()
        }

        nbChat.setOnClickListener {
            if( user === null ) {
                navController.navigate(R.id.navigation_require_login)
                return@setOnClickListener
            } else if ( user.uid == nbUid ) {
                Log.d("dd" , "Cannot Chat With Same User")
                return@setOnClickListener
            }

            val roomRef = db.collection("room")
            val roomDocument = roomRef.document()
            val talkers = arrayListOf(user.uid, nbUid)
            val nChatRoom  = ChatRoom(
                roomRef.id,
                user.uid,
                nbUid,
                nbFarmerName,
                "",
                "",
                talkers,
                Date()
            )

            roomRef
                .whereEqualTo("talkers", talkers)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if( task.result!!.documents.size == 0 ){
                            val newRoomId = roomDocument.id
                            addRoom(roomDocument, nChatRoom)
                            holdChattingRoom(newRoomId)
                        } else if ( task.result!!.documents.size == 1 ){
                            val existingRoomId = task.result!!.documents[0].id
                            holdChattingRoom(existingRoomId)
                        }
                    } else {
                        bundle = bundleOf()
                        Log.d("TAG", "Error getting room check documents: ", task.exception)
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

    private fun holdChattingRoom(roomId: String){
        val args = requireArguments()
        bundle = bundleOf(
            "roomId" to roomId,
            "receiverUid" to args.getString("uid"),
            "receiverName" to args.getString("farmerName"),
            "directToChat" to true
        )
        navController.navigate(R.id.action_neighbor_detail_to_navigation_chat_room, bundle)
    }
}