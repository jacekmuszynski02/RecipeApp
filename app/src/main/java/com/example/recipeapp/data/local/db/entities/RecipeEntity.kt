package com.example.recipeapp.data.local.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val instructions: String,
    val imageUrl: String,
    val timestamp: Long = System.currentTimeMillis()
)
