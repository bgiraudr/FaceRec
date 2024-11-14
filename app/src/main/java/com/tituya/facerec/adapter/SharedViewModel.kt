package com.tituya.facerec.adapter

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    private val _selectedData = MutableLiveData<Bitmap?>()
    val selectedData: LiveData<Bitmap?> get() = _selectedData

    fun faceSelected(bmp: Bitmap) {
        _selectedData.value = bmp
    }

    // first face is shared between fragments, so we need to clear it when it's deselected in one of them
    fun firstFaceDeselected() {
        _selectedData.value = null
    }
}