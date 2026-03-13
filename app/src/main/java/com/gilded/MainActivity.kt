package com.gilded

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.gilded.fragments.ExpensesFragment
import com.gilded.fragments.HomeFragment
import com.gilded.fragments.ReceiptCreatorFragment
import com.gilded.testsamples.TestCategories
import com.gilded.testsamples.TestReceipts
import com.gilded.viewmodels.CategoriesViewModel
import com.gilded.viewmodels.ReceiptsViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.graphics.toColorInt
import com.gilded.services.ReceiptAPI
import kotlinx.coroutines.CoroutineScope

class MainActivity : AppCompatActivity() {

    lateinit var mainConstraintLayout: ConstraintLayout

    private val receiptsViewModel: ReceiptsViewModel by viewModels()
    private val categoriesViewModel: CategoriesViewModel by viewModels()

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

        receiptsViewModel.loadReceipts()

        for (category in TestCategories.categories) {
            categoriesViewModel.addCategory(category)
        }

        val homeFragment = HomeFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, homeFragment)
            .commit()

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationMenu)

        bottomNavigationView.setOnItemSelectedListener { clickedItem ->
            val selectedFragment: Fragment? = when (clickedItem.itemId) {
                R.id.home -> HomeFragment()
                R.id.add -> ReceiptCreatorFragment()
                R.id.expenses -> ExpensesFragment()

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
        bottomNavigationVisibleHashMap["ExpensesFragment"] = true

        supportFragmentManager.registerFragmentLifecycleCallbacks(
            object: FragmentManager.FragmentLifecycleCallbacks() {

                override fun onFragmentViewCreated(
                    fragmentManager: FragmentManager,
                    fragment: Fragment,
                    view: View,
                    savedInstanceState: Bundle?
                ) {
                    val fragmentName = fragment::class.java.simpleName

                    bottomNavigationView.visibility = if (bottomNavigationVisibleHashMap.get(fragmentName) != null) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
                }

            },

            true
        )
    }
}