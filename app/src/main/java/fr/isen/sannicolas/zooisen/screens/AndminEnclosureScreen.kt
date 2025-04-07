package fr.isen.sannicolas.zooisen.screens

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import fr.isen.sannicolas.zooisen.database.Biome
import fr.isen.sannicolas.zooisen.database.Database
import fr.isen.sannicolas.zooisen.database.Enclosure

@Composable
fun AdminEnclosureScreen(navController: NavHostController) {
    val context = LocalContext.current
    var biomes by remember { mutableStateOf<List<Biome>>(emptyList()) }

    // Fonction pour rafraîchir les biomes après modification
    fun refreshBiomes() {
        Database.fetchBiomes(
            onSuccess = { biomes = it },
            onFailure = { println("❌ Erreur lors du chargement des biomes : ${it.message}") }
        )
    }

    // Charger les biomes au lancement de l'écran
    LaunchedEffect(Unit) {
        Database.fetchBiomes(
            onSuccess = { biomes = it },
            onFailure = { println("❌ Erreur lors du chargement des biomes : ${it.message}") }
        )
    }

    val enclosuresWithBiome = biomes.flatMap { biome ->
        biome.enclosures.map { enclosure -> biome.name to enclosure }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Titre de la page
        Text(
            text = "Gestion des Enclos",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            modifier = Modifier.padding(bottom = 24.dp) // Espacement au bas du titre
        )

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(enclosuresWithBiome, key = { it.second.id }) { (biomeName, enclosure) ->
                var isOpen by remember { mutableStateOf(enclosure.isOpen) }

                // Carte de chaque enclos
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp) // Espacement entre chaque enclos
                        .background(Color(0xFFFFF8E1)), // Fond doux
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp) // Espacement intérieur
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Enclos ${enclosure.id} ($biomeName)",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF3E2723) // Couleur foncée pour le texte
                            )
                            Spacer(modifier = Modifier.height(8.dp)) // Espacement vertical
                            Text(
                                text = if (isOpen) "Statut : Ouvert ✅" else "Statut : Fermé ❌",
                                color = if (isOpen) Color.Green else Color.Red,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp)) // Espacement entre le texte et le switch

                        Switch(
                            checked = isOpen,
                            onCheckedChange = { isChecked ->
                                isOpen = isChecked
                                // Mise à jour de Firebase immédiatement
                                Database.updateEnclosureStatus(
                                    biomeName,
                                    enclosure.id,
                                    isChecked,
                                    onSuccess = {
                                        Toast.makeText(
                                            context,
                                            "État de l'enclos mis à jour",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    onFailure = {
                                        Toast.makeText(
                                            context,
                                            "Erreur de mise à jour",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                )
                            }
                        )
                    }
                }
            }
        }

        // Bouton de retour
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { navController.popBackStack() },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text("Retour", color = Color.Black)
        }
    }
}
