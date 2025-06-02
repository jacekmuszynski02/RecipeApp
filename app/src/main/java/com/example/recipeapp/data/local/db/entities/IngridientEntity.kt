package com.example.recipeapp.data.local.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "ingredients",
    foreignKeys = [ForeignKey(
        entity = RecipeEntity::class,
        parentColumns = ["id"],
        childColumns = ["recipeId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class IngredientEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val recipeId: Int,
    val name: String,
    val amount: String,
    val checked: Boolean = false
)
