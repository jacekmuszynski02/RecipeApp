package com.example.recipeapp.data.remote.model

import com.example.recipeapp.data.local.db.entities.IngredientEntity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MealResponse(
    val meals: List<Meal>
)

@JsonClass(generateAdapter = true)
data class Meal(
    @Json(name = "idMeal") val idMeal: String,
    @Json(name = "strMeal") val strMeal: String,
    @Json(name = "strInstructions") val strInstructions: String,
    @Json(name = "strMealThumb") val strMealThumb: String,
    @Json(name = "strIngredient1") val strIngredient1: String?,
    @Json(name = "strIngredient2") val strIngredient2: String?,
    @Json(name = "strIngredient3") val strIngredient3: String?,
    @Json(name = "strIngredient4") val strIngredient4: String?,
    @Json(name = "strIngredient5") val strIngredient5: String?,
    @Json(name = "strIngredient6") val strIngredient6: String?,
    @Json(name = "strIngredient7") val strIngredient7: String?,
    @Json(name = "strIngredient8") val strIngredient8: String?,
    @Json(name = "strIngredient9") val strIngredient9: String?,
    @Json(name = "strIngredient10") val strIngredient10: String?,
    @Json(name = "strIngredient11") val strIngredient11: String?,
    @Json(name = "strIngredient12") val strIngredient12: String?,
    @Json(name = "strIngredient13") val strIngredient13: String?,
    @Json(name = "strIngredient14") val strIngredient14: String?,
    @Json(name = "strIngredient15") val strIngredient15: String?,
    @Json(name = "strIngredient16") val strIngredient16: String?,
    @Json(name = "strIngredient17") val strIngredient17: String?,
    @Json(name = "strIngredient18") val strIngredient18: String?,
    @Json(name = "strIngredient19") val strIngredient19: String?,
    @Json(name = "strIngredient20") val strIngredient20: String?,
    @Json(name = "strMeasure1") val strMeasure1: String?,
    @Json(name = "strMeasure2") val strMeasure2: String?,
    @Json(name = "strMeasure3") val strMeasure3: String?,
    @Json(name = "strMeasure4") val strMeasure4: String?,
    @Json(name = "strMeasure5") val strMeasure5: String?,
    @Json(name = "strMeasure6") val strMeasure6: String?,
    @Json(name = "strMeasure7") val strMeasure7: String?,
    @Json(name = "strMeasure8") val strMeasure8: String?,
    @Json(name = "strMeasure9") val strMeasure9: String?,
    @Json(name = "strMeasure10") val strMeasure10: String?,
    @Json(name = "strMeasure11") val strMeasure11: String?,
    @Json(name = "strMeasure12") val strMeasure12: String?,
    @Json(name = "strMeasure13") val strMeasure13: String?,
    @Json(name = "strMeasure14") val strMeasure14: String?,
    @Json(name = "strMeasure15") val strMeasure15: String?,
    @Json(name = "strMeasure16") val strMeasure16: String?,
    @Json(name = "strMeasure17") val strMeasure17: String?,
    @Json(name = "strMeasure18") val strMeasure18: String?,
    @Json(name = "strMeasure19") val strMeasure19: String?,
    @Json(name = "strMeasure20") val strMeasure20: String?
)

fun Meal.getIngredients(): List<IngredientEntity> {
    val ingredients = mutableListOf<IngredientEntity>()
    for (i in 1..20) {
        val name = this.javaClass.getDeclaredField("strIngredient$i").apply { isAccessible = true }
            .get(this) as? String
        val amount = this.javaClass.getDeclaredField("strMeasure$i").apply { isAccessible = true }
            .get(this) as? String

        if (!name.isNullOrBlank()) {
            ingredients.add(
                IngredientEntity(
                    recipeId = this.idMeal.toInt(),
                    name = name.trim(),
                    amount = amount?.trim().orEmpty()
                )
            )
        }
    }
    return ingredients
}
