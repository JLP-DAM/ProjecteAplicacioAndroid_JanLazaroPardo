package com.gilded

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.gilded.fragments.TransactionsFragment
import com.gilded.fragments.HomeFragment
import com.gilded.fragments.ReceiptCreatorFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.gilded.fragments.LoginFragment
import com.gilded.services.UsageData
import com.gilded.services.UsageData.receiptCreations
import com.gilded.services.UsageData.receiptDeletions
import com.gilded.services.UsageData.usageTime
import com.gilded.viewmodels.CurrentUserViewModel
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlin.getValue

class MainActivity : AppCompatActivity() {

    lateinit var mainConstraintLayout: ConstraintLayout

    private val currentUserViewModel: CurrentUserViewModel by viewModels()

    var startTime = System.currentTimeMillis()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { false }

        setContentView(R.layout.activity_main)

        mainConstraintLayout = findViewById(R.id.main)

        val gradientPoints = intArrayOf(
            resources.getColor(R.color.black_tonal1),
            resources.getColor(R.color.black),
        )

        val backgroundGradientDrawable = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, gradientPoints)

        mainConstraintLayout.background = backgroundGradientDrawable

        val loginFragment = LoginFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, loginFragment)
            .commit()

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationMenu)

        bottomNavigationView.setOnItemSelectedListener { clickedItem ->
            val selectedFragment: Fragment? = when (clickedItem.itemId) {
                R.id.home -> HomeFragment()
                R.id.add -> ReceiptCreatorFragment()
                R.id.transactions -> TransactionsFragment()

                else -> null
            }

            if (selectedFragment != null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, selectedFragment)
                    .commit()
            }

            true
        }

        val bottomNavigationVisibleHashMap = HashMap<String, Boolean?>()

        bottomNavigationVisibleHashMap["HomeFragment"] = true
        bottomNavigationVisibleHashMap["TransactionsFragment"] = true

        supportFragmentManager.registerFragmentLifecycleCallbacks(
            object: FragmentManager.FragmentLifecycleCallbacks() {

                override fun onFragmentViewCreated(
                    fragmentManager: FragmentManager,
                    fragment: Fragment,
                    view: View,
                    savedInstanceState: Bundle?
                ) {
                    val fragmentName = fragment::class.java.simpleName

                    UsageData.mostFrequentedFragments[fragmentName] = (UsageData.mostFrequentedFragments[fragmentName] ?: 0) + 1

                    bottomNavigationView.visibility = if (bottomNavigationVisibleHashMap[fragmentName] != null) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
                }

            },

            true
        )
    }

    override fun onResume() {
        super.onResume()

        startTime = System.currentTimeMillis()
    }

    override fun onPause() {
        super.onPause()

        UsageData.usageTime = UsageData.usageTime + (System.currentTimeMillis() - startTime)
    }

    override fun onDestroy() {
        super.onDestroy()

        UsageData.usageTime = UsageData.usageTime + (System.currentTimeMillis() - startTime)

        saveToFirebase()
    }

    private val firestoreDatabase: FirebaseFirestore by lazy { Firebase.firestore }

    fun saveToFirebase() {
        Log.d("I'm supposed to be saving", "yeah true" + GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this))

        val usageData = hashMapOf<String, Number>(
            "receiptCreations" to UsageData.receiptCreations,
            "receiptDeletions" to UsageData.receiptDeletions,
            "usageTime" to UsageData.usageTime,
        )

        firestoreDatabase.collection("usageStats").document(currentUserViewModel.user.value!!.id!!.toString())
            .set(usageData)
            .addOnSuccessListener {}
            .addOnFailureListener {}
    }

    fun getFromFirebase() {
        Log.d("I'm supposed to be getting", "yeah true" + GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this))

        firestoreDatabase.collection("usageStats").document(currentUserViewModel.user.value!!.id!!.toString())
            .get()
            .addOnSuccessListener { doc ->
                if (doc != null && doc.exists()) {
                    receiptCreations = doc.getDouble("receiptCreations")?.toInt() ?: 0
                    receiptDeletions = doc.getDouble("receiptDeletions")?.toInt() ?: 0
                    usageTime = doc.getDouble("usageTime") ?: 0.0
                }

            }
            .addOnFailureListener {

            }
    }
}