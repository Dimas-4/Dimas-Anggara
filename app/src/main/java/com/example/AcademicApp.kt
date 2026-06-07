package com.example

import android.app.Application
import androidx.room.Room
import com.example.data.AppDatabase

class AcademicApp : Application() {
    companion object {
        lateinit var database: AppDatabase
            private set
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "naskahpro_academic.db"
        ).fallbackToDestructiveMigration().build()
    }
}
