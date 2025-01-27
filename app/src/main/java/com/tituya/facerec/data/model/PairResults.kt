package com.tituya.facerec.data.model

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

class PairResults(val picturePath: String, val score: Double) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readDouble()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(picturePath)
        parcel.writeDouble(score)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PairResults> {
        override fun createFromParcel(parcel: Parcel): PairResults {
            return PairResults(parcel)
        }

        override fun newArray(size: Int): Array<PairResults?> {
            return arrayOfNulls(size)
        }
    }
}