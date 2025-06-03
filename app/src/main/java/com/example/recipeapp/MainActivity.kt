package com.example.recipeapp

import ads_mobile_sdk.h6
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.recipeapp.data.local.db.AppDatabase
import com.example.recipeapp.data.local.db.entities.RecipeEntity
import com.example.recipeapp.data.local.db.entities.RecipeWithIngredients
import com.example.recipeapp.data.remote.RetrofitInstance
import com.example.recipeapp.data.remote.model.Meal
import com.example.recipeapp.data.remote.model.getIngredients
import kotlinx.coroutines.launch
import androidx.compose.ui.text.style.TextAlign


class MainActivity : ComponentActivity() {

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = (application as MyApp).database

        setContent {
            com.example.recipeapp.ui.theme.RecipeAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController, startDestination = "recipeScreen") {
                        composable("recipeScreen") {
                            RecipeScreen(db, navController)
                        }
                        composable("randomRecipeScreen") {
                            RandomRecipeScreen(db, navController)
                        }
                        composable("recipeDetailScreen/{recipeId}") { backStackEntry ->
                            val recipeId = backStackEntry.arguments?.getString("recipeId")?.toLongOrNull()
                            recipeId?.let {
                                RecipeDetailScreen(recipeId = it, db = db, navController = navController)
                            }
                        }
                        composable("shoppingListScreen/{id}") { backStackEntry ->
                            val recipeId = backStackEntry.arguments?.getString("id")?.toLongOrNull() ?: return@composable
                            ShoppingListScreen(recipeId, db, navController)
                        }
                    }
                }
            }

        }
    }
}

@Composable
fun RecipeScreen(db: AppDatabase, navController: NavHostController) {
    var recipes by remember { mutableStateOf<List<RecipeWithIngredients>>(emptyList()) }

    val coroutineScope = rememberCoroutineScope()

    fun loadRecipes() {
        coroutineScope.launch {
            recipes = db.recipeDao().getAllRecipes()
        }
    }

    LaunchedEffect(Unit) {
        loadRecipes()
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Button(
            onClick = { navController.navigate("randomRecipeScreen") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Discover more recipes")
        }

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalDivider()

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Saved recipes",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn {
            items(recipes) { recipeWithIngredients ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFF7043)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            navController.navigate("recipeDetailScreen/${recipeWithIngredients.recipe.id}")
                        }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = recipeWithIngredients.recipe.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1f)
                            )

                            IconButton(onClick = {
                                coroutineScope.launch {
                                    db.recipeDao().deleteRecipe(recipeWithIngredients.recipe)
                                    loadRecipes()
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Usuń przepis"
                                )
                            }
                        }

                        AsyncImage(
                            model = recipeWithIngredients.recipe.imageUrl,
                            contentDescription = "Image of ${recipeWithIngredients.recipe.name}",
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 9f)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    }
}




@Composable
fun RandomRecipeScreen(db: AppDatabase, navController: NavHostController) {
    var meal by remember { mutableStateOf<Meal?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    suspend fun fetchNewUniqueMeal() {
        while (true) {
            val newMeal = try {
                RetrofitInstance.api.getRandomMeal().meals.firstOrNull()
            } catch (e: Exception) {
                null
            }

            val exists = newMeal?.idMeal?.toIntOrNull()?.let { db.recipeDao().getRecipeById(it) } != null

            if (newMeal != null && !exists) {
                meal = newMeal
                listState.animateScrollToItem(0)
                break
            }
        }
    }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            fetchNewUniqueMeal()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                PaddingValues(
                    top = WindowInsets.systemBars.asPaddingValues().calculateTopPadding() + 16.dp
                )
            )

    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding(),
                    bottom = 100.dp,
                    start = 16.dp,
                    end = 16.dp
                ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        )
        {
            meal?.let { currentMeal ->
                item {
                    AsyncImage(
                        model = currentMeal.strMealThumb,
                        contentDescription = currentMeal.strMeal,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                item {
                    Text(
                        text = currentMeal.strMeal,
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFF7043)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Text(
                            text = currentMeal.strInstructions,
                            modifier = Modifier
                                .padding(16.dp)
                        )
                    }
                }
            } ?: item {
                Text("Loading recipe or error")
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    coroutineScope.launch {
                        fetchNewUniqueMeal()
                    }
                },
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Skip",
                    modifier = Modifier.size(32.dp)
                )
            }

            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Go back")
            }

            IconButton(
                onClick = {
                    coroutineScope.launch {
                        meal?.let { currentMeal ->
                            val recipeEntity = RecipeEntity(
                                id = currentMeal.idMeal.toInt(),
                                name = currentMeal.strMeal,
                                instructions = currentMeal.strInstructions,
                                imageUrl = currentMeal.strMealThumb,
                                timestamp = System.currentTimeMillis()
                            )
                            val ingredients = currentMeal.getIngredients()
                            db.recipeDao().insertRecipe(recipeEntity)
                            db.recipeDao().insertIngredients(ingredients)
                            fetchNewUniqueMeal()
                        }
                    }
                },
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Save",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
fun RecipeDetailScreen(
    recipeId: Long,
    db: AppDatabase,
    navController: NavHostController
) {
    var recipeWithIngredients by remember { mutableStateOf<RecipeWithIngredients?>(null) }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(recipeId) {
        coroutineScope.launch {
            recipeWithIngredients = db.recipeDao().getRecipeWithIngredientsById(recipeId)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.systemBars.asPaddingValues())
    ) {
        recipeWithIngredients?.let { rwi ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 72.dp)
                    .padding(16.dp)
            ) {
                Text(
                    text = rwi.recipe.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                AsyncImage(
                    model = rwi.recipe.imageUrl,
                    contentDescription = "Image of ${rwi.recipe.name}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(12.dp))
                        .padding(bottom = 16.dp),
                    contentScale = ContentScale.Crop
                )

                Text(
                    text = "Ingredients:",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                rwi.ingredients.forEach { ingredient ->
                    Text(
                        text = if (ingredient.amount.isNullOrBlank())
                            "- ${ingredient.name}"
                        else
                            "- ${ingredient.name} (${ingredient.amount})"
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Recipe:",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = rwi.recipe.instructions ?: "Empty",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        } ?: run {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }


        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.weight(1f)
            ) {
                Text("Go back")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    navController.navigate("shoppingListScreen/${recipeId}")
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Shopping list")
            }

        }
    }
}

@Composable
fun ShoppingListScreen(recipeId: Long, db: AppDatabase, navController: NavHostController) {
    var ingredients by remember { mutableStateOf(emptyList<com.example.recipeapp.data.local.db.entities.IngredientEntity>()) }
    var recipeName by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(recipeId) {
        coroutineScope.launch {
            val recipeWithIngredients = db.recipeDao().getRecipeWithIngredientsById(recipeId)
            recipeWithIngredients?.let {
                ingredients = it.ingredients
                recipeName = it.recipe.name
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = recipeName,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(ingredients) { ingredient ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Checkbox(
                        checked = ingredient.checked,
                        onCheckedChange = { checked ->
                            coroutineScope.launch {
                                db.recipeDao().updateIngredientChecked(ingredient.id.toLong(), checked)
                                val updated = db.recipeDao().getRecipeWithIngredientsById(recipeId)
                                updated?.let { ingredients = it.ingredients }
                            }
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (ingredient.amount.isNullOrBlank())
                            ingredient.name
                        else
                            "${ingredient.name} (${ingredient.amount})"
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.weight(1f)
            ) {
                Text("Go back")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        db.recipeDao().clearAllChecked(recipeId)
                        val updated = db.recipeDao().getRecipeWithIngredientsById(recipeId)
                        updated?.let { ingredients = it.ingredients }
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Clear all")
            }
        }
    }
}










