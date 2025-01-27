package com.tituya.facerec.data.room

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PersonEntity::class, FaceEntity::class], version = 1)
abstract class FaceRecDatabase : RoomDatabase() {
    abstract fun personDao(): PersonDAO
    abstract fun faceDao(): FaceDAO

    companion object {
        @Volatile
        private var INSTANCE: FaceRecDatabase? = null

        fun getDatabase(context: android.content.Context): FaceRecDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    FaceRecDatabase::class.java,
                    "facerec_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}