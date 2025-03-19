package fr.isen.sannicolas.zooisen.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.core.graphics.toColorInt
import fr.isen.sannicolas.zooisen.R
import fr.isen.sannicolas.zooisen.database.Biome
import fr.isen.sannicolas.zooisen.database.Database
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BiomeScreen(navController: NavHostController) {
    var biomes by remember { mutableStateOf<List<Biome>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        Database.fetchBiomes(
            onSuccess = { biomes = it.take(6) },
            onFailure = { errorMessage = it.message }
        )
    }

    Scaffold(
        containerColor = Color(0xFFF5E9D2),
        topBar = {
            TopAppBar(
                title = { Text("Parc animalier La Barben", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFD4C1A4))
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (errorMessage != null) {
                    Text(text = "Erreur : $errorMessage", color = Color.Red, modifier = Modifier.padding(8.dp))
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(biomes) { biome -> BiomeItem(biome, navController) }
                        item { ParkServicesCard(navController) }
                        item { WazooCard(navController) }
                    }
                }
            }
        }
    )
}

@Composable
fun BiomeItem(biome: Biome, navController: NavHostController) {
    val safeColor = try {
        if (biome.color.isNotBlank()) Color(biome.color.toColorInt()) else Color.Gray
    } catch (e: Exception) {
        Color.Gray
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clickable { navController.navigate("enclosures/${biome.name}") },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = safeColor),
        border = BorderStroke(2.dp, Color.White)
    ) {
        Box {
            Image(
                painter = painterResource(id = getBiomeImage(biome.name)),
                contentDescription = biome.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))
                        )
                    )
            )

            Text(
                text = biome.name,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            )
        }
    }
}

@Composable
fun ParkServicesCard(navController: NavHostController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clickable { navController.navigate("park_services") },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50)),
        border = BorderStroke(2.dp, Color.White)
    ) {
        Box {
            Image(
                painter = painterResource(id = R.drawable.parc),
                contentDescription = "Park Services & Map",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))
                        )
                    )
            )

            Text(
                text = "Services du parc & Map",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            )
        }
    }
}

@Composable
fun WazooCard(navController: NavHostController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clickable { navController.navigate("wazoo") },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E88E5)),
        border = BorderStroke(2.dp, Color.White)
    ) {
        Box {
            Image(
                painter = painterResource(id = R.drawable.wazoo),
                contentDescription = "Wazoo - Navigation",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Fit
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))
                        )
                    )
            )

            Text(
                text = "Wazoo - Navigation",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            )
        }
    }
}

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
