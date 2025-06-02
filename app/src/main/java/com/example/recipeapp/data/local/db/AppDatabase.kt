package com.example.recipeapp.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.recipeapp.data.local.db.entities.IngredientEntity
import com.example.recipeapp.data.local.db.entities.RecipeEntity

@Database(
    entities = [RecipeEntity::class, IngredientEntity::class],
    version = 2
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao
}
