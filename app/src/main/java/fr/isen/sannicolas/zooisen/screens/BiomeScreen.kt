package fr.isen.sannicolas.zooisen.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.foundation.background
import fr.isen.sannicolas.zooisen.database.Biome
import androidx.compose.ui.res.painterResource
import fr.isen.sannicolas.zooisen.R
import fr.isen.sannicolas.zooisen.database.Database
import androidx.core.graphics.toColorInt

@Composable
fun BiomeScreen(navController: NavHostController) {
    var biomes by remember { mutableStateOf<List<Biome>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        Database.fetchBiomes(
            onSuccess = { biomes = it.filterIndexed { index, _ -> index < 6 } }, // ✅ Filtrage des 6 premiers biomes
            onFailure = { errorMessage = it.message }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFAA603D))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Liste des Biomes",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (errorMessage != null) {
            Text(
                text = "Erreur : $errorMessage",
                color = MaterialTheme.colorScheme.error
            )
        } else {
            LazyColumn {
                items(biomes) { biome ->
                    BiomeItem(biome, navController)
                }

                // ✅ Ajout de la carte "Park Services"
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable { navController.navigate("park_services") },
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50)) // ✅ Couleur verte
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Park Services",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BiomeItem(biome: Biome, navController: NavHostController) {
    val safeColor = try {
        if (biome.color.isNotBlank()) {
            Color(biome.color.toColorInt()) // ✅ Utilisation de `toColorInt()` pour éviter les erreurs
        } else {
            Color.Gray
        }
    } catch (e: Exception) {
        Color.Gray
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { navController.navigate("enclosures/${biome.name}") },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = safeColor) // ✅ Utilisation sécurisée
    ) {
        Column {
            Image(
                painter = painterResource(id = getBiomeImage(biome.name)),
                contentDescription = biome.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentScale = ContentScale.Crop
            )
            Text(
                text = biome.name,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp),
                color = Color.White
            )
        }
    }
}

// Fonction pour récupérer une image selon le biome
fun getBiomeImage(biomeName: String): Int {
    return when (biomeName) {
        "La Bergerie des reptiles" -> R.drawable.reptile
        "Le Vallon des cascades" -> R.drawable.ara
        "Le Belvédère" -> R.drawable.rhinoceros
        "Le Plateau" -> R.drawable.lion
        "Les Clairières" -> R.drawable.bison
        "Le Bois des pins" -> R.drawable.loup
        else -> R.drawable.default_image
    }
}
