package com.tituya.facerec.adapter

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    private val _selectedData = MutableLiveData<Bitmap>()
    val selectedData: LiveData<Bitmap> get() = _selectedData

    fun faceSelected(bmp: Bitmap) {
        _selectedData.value = bmp
    }
}