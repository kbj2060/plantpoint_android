package com.example.plantpoint.ui.neighbor

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.plantpoint.dto.Neighbor
import com.example.plantpoint.MainActivity
import com.example.plantpoint.R
import com.example.plantpoint.adapter.NeighborAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.yarolegovich.discretescrollview.DSVOrientation
import com.yarolegovich.discretescrollview.DiscreteScrollView
import com.yarolegovich.discretescrollview.transform.ScaleTransformer


@Suppress("UNCHECKED_CAST")
class NeighborFragment : Fragment(),  DiscreteScrollView.OnItemChangedListener<NeighborAdapter.CustomViewHolder>{

    private lateinit var neighborViewModel: NeighborViewModel
    private var db = FirebaseFirestore.getInstance()
    private var farmerRef = db.collection("farm")
    var neighborList: ArrayList<Neighbor> = ArrayList()
    var rv_neighbor: DiscreteScrollView? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        neighborViewModel = ViewModelProvider(this).get(NeighborViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_neighbor, container, false)
        rv_neighbor = root.findViewById(R.id.rv_neighbor)
        val scrollIndex = (activity as MainActivity).neighborScrollIndex

        farmerRef
            .whereEqualTo("repArea", "모현읍")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val newNeighbor = Neighbor(
                        id = document.id,
                        profile = document.data["profile"] as String,
                        repArea = document.data["repArea"] as String,
                        farmerName = document.data["name"] as String,
                        farmerLocation = document.data["location"] as String,
                        crops = document.data["crops"] as Map<String, Int>,
                        uid = document.data["uid"] as String
                    )
                    neighborList.add(newNeighbor)
                }

                initRecyclerView(neighborList)

                if (scrollIndex != null && rv_neighbor!!.adapter!!.itemCount > scrollIndex){
                    rv_neighbor!!.scrollToPosition(scrollIndex)
                }

                (rv_neighbor!!.adapter as NeighborAdapter).setItemClickListener( object : NeighborAdapter.ItemClickListener{
                    override fun onClick(view: View, position: Int) {
                        navigateToDetail(neighborList[position])
                    }
                })
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents: ", exception)
            }
        return root
    }

    override fun onStop() {
        super.onStop()
        neighborList = ArrayList()
    }

    override fun onCurrentItemChanged( viewHolder: NeighborAdapter.CustomViewHolder?, adapterPosition: Int ) {
        (activity as MainActivity).changeNeighborScrollIndex(adapterPosition)
    }

    fun navigateToDetail(neighbor : Neighbor) {
        val navController = Navigation.findNavController(activity as MainActivity, R.id.nav_host_fragment)
        val bundle = bundleOf(
            "uid" to neighbor.uid,
            "farmerName" to neighbor.farmerName,
            "farmerLocation" to neighbor.farmerLocation,
            "farmerProfile" to neighbor.profile
        )
        navController.navigate(R.id.action_navigation_neighbor_to_neighbor_detail, bundle)
    }

    private fun initRecyclerView(items: ArrayList<Neighbor>) {
        rv_neighbor!!.setOrientation(DSVOrientation.HORIZONTAL)
        rv_neighbor!!.addOnItemChangedListener(this)
        rv_neighbor!!.setItemTransitionTimeMillis(150)
        rv_neighbor!!.setItemTransformer(
            ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build()
        )
        rv_neighbor!!.adapter = NeighborAdapter( neighborList )
    }
}
