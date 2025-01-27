package com.tituya.facerec

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.tituya.facerec.adapter.PersonAdapter
import com.tituya.facerec.data.model.Face
import com.tituya.facerec.data.model.Person
import com.tituya.facerec.data.room.FaceDAO
import com.tituya.facerec.data.room.FaceRecDatabase
import com.tituya.facerec.data.room.PersonDAO
import com.tituya.facerec.databinding.ActivityManageFacesBinding
import com.tituya.facerec.utils.FaceUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream


class ManageFacesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityManageFacesBinding
    private lateinit var adapter: PersonAdapter
    private var personId: Long = 0

    private lateinit var database: FaceRecDatabase
    private lateinit var faceDao: FaceDAO
    private lateinit var personDao: PersonDAO
    private lateinit var pickMultipleMedia: ActivityResultLauncher<PickVisualMediaRequest>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageFacesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FaceRecDatabase.getDatabase(applicationContext)
        personDao = database.personDao()
        faceDao = database.faceDao()

        val recy = binding.personRecycler
        recy.layoutManager = LinearLayoutManager(this)
        adapter = PersonAdapter(this, { person -> deletePerson(person) }, { person -> addFace(person) }, { person, face -> deleteFacePerson(person, face) })
        recy.adapter = adapter
        binding.addPersonButton.isEnabled = false

        loadPersons()

        binding.addPersonButton.setOnClickListener {
            addPerson(binding.personName.text.toString())
            Toast.makeText(this, "Done !", Toast.LENGTH_SHORT).show()
        }

        binding.personName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.addPersonButton.isEnabled = s.toString().isNotEmpty()
            }
        })

        pickMultipleMedia =
            registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uris ->
                if (uris.isNotEmpty()) {
                    for (uri in uris) {
                        FaceUtils.detectFaces(this, uri, onFaceDetected = { bounds ->
                            val facesBitmap = FaceUtils.cropImageToFace(this, uri, bounds)
                            for (face in facesBitmap) {
                                createImageUri()?.let { uri ->
                                    saveFaceImageToUri(uri, face)
                                    val embed = FaceUtils.computeEmbedding(this, face)
                                    if(embed != null) {
                                        val faceModel = Face(
                                            personId = personId,
                                            embeddings = embed,
                                            faceImage = uri.toString()
                                        )

                                        CoroutineScope(Dispatchers.IO).launch {
                                            faceDao.insertFace(faceModel.toEntity(personId = personId))
                                            val personWithFaces =
                                                personDao.getPersonWithFaces(personId).toModel()
                                            withContext(Dispatchers.Main) {
                                                adapter.updatePerson(personWithFaces)
                                            }
                                        }
                                    }
                                }
                            }
                        })
                    }
                }
            }

    }

    private fun addPerson(name: String) {
        val newPerson = Person(name = name)
        CoroutineScope(Dispatchers.IO).launch {
            val id = personDao.insertPerson(newPerson.toEntity())
            val personDb = newPerson.copy(id = id)
            withContext(Dispatchers.Main) {
                adapter.addPerson(personDb)
            }
        }
    }

    private fun loadPersons() {
        CoroutineScope(Dispatchers.IO).launch {
            val personsFaces = personDao.getAllPersonsWithFaces().map { it.toModel() }
            withContext(Dispatchers.Main) {
                adapter.updateData(personsFaces)
            }
        }
    }

    private fun deletePerson(person: Person) {
        CoroutineScope(Dispatchers.IO).launch {
            personDao.deletePerson(person.toEntity(person.id))
        }
        adapter.deletePerson(person)
    }

    private fun addFace(person: Person) {
        personId = person.id
        pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun createImageUri(): Uri? {
        val contentResolver = contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "image_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MyApp")
        }
        return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    }

    private fun saveFaceImageToUri(uri: Uri, faceBitmap: Bitmap) {
        val outputStream = contentResolver.openOutputStream(uri)
        if(outputStream != null) faceBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream?.close()
    }

    private fun deleteFacePerson(person: Person, face: Face) {
        CoroutineScope(Dispatchers.IO).launch {
            faceDao.deleteFace(face.toEntity(personId = person.id))
            val personWithFaces = personDao.getPersonWithFaces(person.id).toModel()
            withContext(Dispatchers.Main) {
                adapter.updatePerson(personWithFaces)
            }
        }
    }
}