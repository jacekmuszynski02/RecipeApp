package com.example.recipeapp.data.remote

import com.example.recipeapp.data.remote.api.MealApiService
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitInstance {
    val api: MealApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://www.themealdb.com/api/json/v1/1/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(MealApiService::class.java)
    }
}
