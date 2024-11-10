package com.tituya.facerec

import android.animation.Animator
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.CarouselSnapHelper
import com.google.android.material.carousel.HeroCarouselStrategy
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
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

        findViewById<ExtendedFloatingActionButton>(R.id.add_face_fab).setOnClickListener { showMenu() }
        findViewById<FloatingActionButton>(R.id.fab_import).setOnClickListener {

        }
    }

    private fun showMenu() {
        val fabCamera = findViewById<FloatingActionButton>(R.id.fab_camera)
        val fabImport = findViewById<FloatingActionButton>(R.id.fab_import)
        animateFab(fabCamera, -175f, 0f, 200)
        animateFab(fabImport, -350f, 0f, 200)
    }

    private fun animateFab(fab: FloatingActionButton, translationY: Float, translationX: Float, duration: Long) {
        var state = fab.visibility == View.GONE
        fab.animate().translationY(if(state) translationY else 0f).translationX(if(state) translationX else 0f).alpha(if(state) 1f else 0f).setDuration(duration).setListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                if(state) fab.visibility = View.VISIBLE
            }
            override fun onAnimationEnd(animation: Animator) {
                if(!state) fab.visibility = View.GONE
            }
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        }).start()
    }
}