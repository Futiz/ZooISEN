package fr.isen.sannicolas.zooisen.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import fr.isen.sannicolas.zooisen.database.Biome
import fr.isen.sannicolas.zooisen.database.Database
import fr.isen.sannicolas.zooisen.database.Enclosure

@Composable
fun EnclosureScreen(navController: NavHostController, biomeName: String) {
    var selectedBiome by remember { mutableStateOf<Biome?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        Database.fetchBiomes(
            onSuccess = { biomes -> selectedBiome = biomes.find { it.name == biomeName } },
            onFailure = { errorMessage = it.message }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFAA603D)) // Fond de la page
            .padding(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Enclos de $biomeName",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (errorMessage != null) {
                Text(text = "Erreur : $errorMessage", color = MaterialTheme.colorScheme.error)
            } else {
                LazyColumn {
                    selectedBiome?.enclosures?.let { enclosures ->
                        items(enclosures) { enclosure ->
                            EnclosureCard(enclosure, navController) // ✅ Utilisation correcte d'EnclosureCard
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text("Retour aux Biomes", color = Color.Black)
            }
        }
    }
}

@Composable
fun EnclosureCard(enclosure: Enclosure, navController: NavHostController) {
    val safeColor = try {
        Color(android.graphics.Color.parseColor("#FFD38B")) // 🔹 Couleur HEX propre
    } catch (e: IllegalArgumentException) {
        Color.Gray // 🔹 Sécurité en cas d'erreur
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = safeColor) // ✅ Sécurisé
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Enclos ${enclosure.id}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))

            // 🔹 Liste des animaux gardés dans l'enclos
            Text(
                text = "Animaux :",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            if (enclosure.animals.isNotEmpty()) {
                Column {
                    enclosure.animals.forEach { animal ->
                        Text(
                            text = "• ${animal.name}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black
                        )
                    }
                }
            } else {
                Text(
                    text = "Aucun animal",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bouton pour aller aux commentaires de l'enclos
            Button(
                onClick = {
                    navController.navigate("enclosure_comments/{biomeName}/{enclosure.id}") // ✅ Navigation vers les commentaires
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Commenter")
            }
        }
    }
}

