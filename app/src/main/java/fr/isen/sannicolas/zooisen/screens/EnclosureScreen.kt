package fr.isen.sannicolas.zooisen.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import fr.isen.sannicolas.zooisen.database.Database
import fr.isen.sannicolas.zooisen.database.Biome
import fr.isen.sannicolas.zooisen.database.Comment

@Composable
fun EnclosureScreen(navController: NavHostController, biomeName: String, enclosureId: String) {
    var selectedBiome by remember { mutableStateOf<Biome?>(null) }
    var comments by remember { mutableStateOf<List<Comment>>(emptyList()) }
    var newComment by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // 🔹 Charger les biomes et commentaires
    LaunchedEffect(Unit) {
        Database.fetchBiomes(
            onSuccess = { biomes -> selectedBiome = biomes.find { it.name == biomeName } },
            onFailure = { errorMessage = it.message }
        )

        Database.fetchComments(enclosureId,
            onSuccess = { comments = it },
            onFailure = { errorMessage = it.message }
        )
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Enclos de $biomeName :", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))

        selectedBiome?.let { biome ->
            biome.enclosures.forEach { enclosure ->
                Text(text = "- Enclos ${enclosureId}", style = MaterialTheme.typography.bodyLarge)

                enclosure.animals.forEach { animal ->
                    Text(text = "   • ${animal.name}", style = MaterialTheme.typography.bodyMedium)
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 🔹 Section Commentaires
        Text(text = "Commentaires :", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.fillMaxHeight(0.5f)) {
            items(comments) { comment ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(text = comment.author, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
                        Text(text = comment.text, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 🔹 Ajouter un commentaire
        OutlinedTextField(
            value = newComment,
            onValueChange = { newComment = it },
            label = { Text("Ajouter un commentaire") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (newComment.isNotBlank()) {
                    Database.addComment(
                        enclosureId = enclosureId,
                        author = "Utilisateur",
                        text = newComment,
                        onSuccess = { newComment = "" },
                        onFailure = { errorMessage = it.message }
                    )
                }
            },
            modifier = Modifier.align(alignment = androidx.compose.ui.Alignment.End)
        ) {
            Text("Envoyer")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.popBackStack() }) {
            Text("Retour aux Biomes")
        }
    }
}
