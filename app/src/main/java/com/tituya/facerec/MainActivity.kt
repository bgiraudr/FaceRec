package com.tituya.facerec

import android.animation.Animator
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationBarView
import com.tituya.facerec.adapter.MainFaceAdapter
import com.tituya.facerec.adapter.SharedViewModel
import com.tituya.facerec.ui.intent.FindFragment
import com.tituya.facerec.ui.intent.MainFragment
import com.tituya.facerec.utils.FaceUtils


class MainActivity : AppCompatActivity() {
    private val sharedViewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //TODO REMOVE DEBUG
        Glide.get(this).clearMemory()
        Thread {
            Glide.get(this).clearDiskCache()
        }.start()

        val adapter = MainFaceAdapter(sharedViewModel)
        val carousel = findViewById<RecyclerView>(R.id.carousel_recycler_view)

        carousel.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)
        carousel.adapter = adapter
        val pickMultipleMedia =
            registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uris ->
                if (uris.isNotEmpty()) {
                    for (uri in uris) {
                        FaceUtils.detectFaces(this, uri, onFaceDetected = { bounds ->
                            adapter.addFaceWithBounds(this, uri, bounds)
                        })
                    }
                }
            }

        findViewById<NavigationBarView>(R.id.bottom_navigation).setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.item_1 -> {
                    if (supportFragmentManager.findFragmentById(R.id.navigationHost) !is MainFragment) {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.navigationHost, MainFragment()).commit()
                        findViewById<TextView>(R.id.page_title).text = "Compare two faces"
                    }
                    true
                }
                R.id.item_2 -> {
                    if (supportFragmentManager.findFragmentById(R.id.navigationHost) !is FindFragment) {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.navigationHost, FindFragment()).commit()
                        findViewById<TextView>(R.id.page_title).text = "Find the person"
                    }
                    true
                }
                else -> false
            }
        }

        findViewById<ExtendedFloatingActionButton>(R.id.add_face_fab).setOnClickListener { showMenu() }
        findViewById<FloatingActionButton>(R.id.fab_import).setOnClickListener {
            pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
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