package com.tituya.facerec

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tituya.facerec.adapter.PersonAdapter
import com.tituya.facerec.databinding.ActivityManageFacesBinding
import com.tituya.facerec.models.Person

class ManageFacesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityManageFacesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_faces)
        val recy = findViewById<RecyclerView>(R.id.personRecycler)
        recy.layoutManager = LinearLayoutManager(this)
        val adapter = PersonAdapter(this)
        recy.adapter = adapter
        adapter.addPerson(Person("yo1", mutableListOf(R.drawable.ic_launcher_background, R.drawable.ic_launcher_background,R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background)))
        adapter.addPerson(Person("yo2", mutableListOf(R.drawable.ic_launcher_background, R.drawable.ic_launcher_background)))
        adapter.addPerson(Person("yo3", mutableListOf(R.drawable.ic_launcher_background)))
    }
}