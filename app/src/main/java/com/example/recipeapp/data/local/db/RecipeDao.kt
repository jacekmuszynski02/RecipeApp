package com.example.recipeapp.data.local.db

import androidx.room.*
import com.example.recipeapp.data.local.db.entities.IngredientEntity
import com.example.recipeapp.data.local.db.entities.RecipeEntity
import com.example.recipeapp.data.local.db.entities.RecipeWithIngredients

@Dao
interface RecipeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: RecipeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredients(ingredients: List<IngredientEntity>)

    @Delete
    suspend fun deleteRecipe(recipe: RecipeEntity)

    @Transaction
    @Query("SELECT * FROM recipes ORDER BY timestamp ASC")
    suspend fun getAllRecipes(): List<RecipeWithIngredients>

    @Query("SELECT * FROM recipes WHERE id = :recipeId LIMIT 1")
    suspend fun getRecipeById(recipeId: Int): RecipeEntity?

    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    suspend fun getRecipeWithIngredientsById(recipeId: Long): RecipeWithIngredients?

    @Query("UPDATE ingredients SET checked = :isChecked WHERE id = :ingredientId")
    suspend fun updateIngredientChecked(ingredientId: Long, isChecked: Boolean)

    @Query("UPDATE ingredients SET checked = 0 WHERE recipeId = :recipeId")
    suspend fun clearAllChecked(recipeId: Long)
}
