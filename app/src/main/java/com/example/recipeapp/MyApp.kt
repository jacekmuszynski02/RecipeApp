package com.example.recipeapp

import android.app.Application
import androidx.room.Room
import com.example.recipeapp.data.local.db.AppDatabase

class MyApp : Application() {

    lateinit var database: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()

        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "recipes-db"
        )
            .build()

    }
}
