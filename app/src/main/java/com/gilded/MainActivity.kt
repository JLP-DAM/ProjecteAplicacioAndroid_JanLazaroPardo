package com.gilded

import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.WindowInsets
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout


class MainActivity : AppCompatActivity() {

    lateinit var mainConstraintLayout: ConstraintLayout

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

        val tasquesFragment = HomeFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, tasquesFragment)
            .commit()
    }
}