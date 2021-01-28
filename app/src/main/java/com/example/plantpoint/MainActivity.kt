package com.example.plantpointimport android.os.Bundleimport android.util.Logimport androidx.appcompat.app.AppCompatActivityimport androidx.navigation.fragment.findNavControllerimport androidx.navigation.ui.setupWithNavControllerimport com.google.android.material.bottomnavigation.BottomNavigationViewimport com.google.firebase.auth.FirebaseAuthimport com.google.firebase.firestore.FirebaseFirestoreclass MainActivity : AppCompatActivity() {    var neighborScrollIndex: Int? = 0    val user = FirebaseAuth.getInstance().currentUser    private var me : Map<String, Any> = mutableMapOf()    override fun onCreate(savedInstanceState: Bundle?) {        super.onCreate(savedInstanceState)        setContentView(R.layout.activity_main)        supportActionBar?.hide()        val navView: BottomNavigationView = findViewById(R.id.nav_view)        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)        val navController = navHostFragment!!.findNavController()        navController.setGraph(R.navigation.mobile_navigation)        navView.setupWithNavController(navController)    }    fun changeNeighborScrollIndex(index: Int){        neighborScrollIndex = index    }    override fun onResume() {        super.onResume()        updateMe()    }    private fun updateMe () {        FirebaseFirestore            .getInstance()            .collection("user")            .whereEqualTo("uid", user!!.uid)            .get()            .addOnSuccessListener { documents ->                for (document in documents) {                    me = document.data                }            }            .addOnFailureListener { exception ->                Log.w("TAG", "Error getting documents: ", exception)            }    }    fun getMe(): Map<String, Any> {        return me    }}