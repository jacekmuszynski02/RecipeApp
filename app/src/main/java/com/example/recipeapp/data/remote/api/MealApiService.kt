package com.example.recipeapp.data.remote.api

import com.example.recipeapp.data.remote.model.MealResponse
import retrofit2.http.GET

interface MealApiService {
    @GET("random.php")
    suspend fun getRandomMeal(): MealResponse
}
