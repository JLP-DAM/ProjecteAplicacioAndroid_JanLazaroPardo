package com.gilded

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    lateinit var mainConstraintLayout: ConstraintLayout

    private val receiptsViewModel: ReceiptsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        mainConstraintLayout = findViewById<ConstraintLayout>(R.id.main)

        val gradientPoints = intArrayOf(
            Color.parseColor("#121621"),
            Color.parseColor("#06090c"),
        )

        val backgroundGradientDrawable = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, gradientPoints)

        mainConstraintLayout.background = backgroundGradientDrawable

        for (receipt in TestReceipts.receipts) {
            receiptsViewModel.addReceipt(receipt)
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