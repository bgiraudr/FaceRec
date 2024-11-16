package com.tituya.facerec.adapter

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    private val _firstFace = MutableLiveData<Bitmap?>()
    private val _secondFace = MutableLiveData<Bitmap?>()
    val firstFace: LiveData<Bitmap?> get() = _firstFace
    val secondFace: LiveData<Bitmap?> get() = _secondFace

    fun faceSelected(bmp: Bitmap) {
        if(_firstFace.value == null) {
            _firstFace.value = bmp
        } else {
            _secondFace.value = bmp
        }
    }

    fun firstFaceDeselected() {
        _firstFace.value = null
    }

    fun secondFaceDeselected() {
        _secondFace.value = null
    }
}