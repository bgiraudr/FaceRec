package com.tituya.facerec

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.CarouselSnapHelper
import com.google.android.material.carousel.HeroCarouselStrategy
import com.google.android.material.navigation.NavigationBarView
import com.tituya.facerec.adapter.MainFaceAdapter
import com.tituya.facerec.ui.intent.FindFragment
import com.tituya.facerec.ui.intent.MainFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val adapter = MainFaceAdapter(mutableListOf())

        val carousel = findViewById<RecyclerView>(R.id.carousel_recycler_view)
        val snapHelper = CarouselSnapHelper()
        snapHelper.attachToRecyclerView(carousel)
        carousel.layoutManager = CarouselLayoutManager(HeroCarouselStrategy())
        carousel.adapter = adapter

        adapter.addFace("1")
        adapter.addFace("1")
        adapter.addFace("1")
        adapter.addFace("1")
        adapter.addFace("1")
        adapter.addFace("1")
        adapter.addFace("1")
        adapter.addFace("1")

        findViewById<NavigationBarView>(R.id.bottom_navigation).setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.item_1 -> {
                    supportFragmentManager.beginTransaction().replace(R.id.navigationHost, MainFragment()).commit()
                    findViewById<TextView>(R.id.page_title).text = "Compare two faces"
                    true
                }
                R.id.item_2 -> {
                    supportFragmentManager.beginTransaction().replace(R.id.navigationHost, FindFragment()).commit()
                    findViewById<TextView>(R.id.page_title).text = "Find the person"
                    true
                }
                else -> false
            }
        }
    }
}