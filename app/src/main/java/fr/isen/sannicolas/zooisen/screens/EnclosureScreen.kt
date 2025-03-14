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
import fr.isen.sannicolas.zooisen.R
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.clickable
import com.google.firebase.database.FirebaseDatabase


@Composable
fun EnclosureScreen(navController: NavHostController, biomeId: String) {
    var selectedBiome by remember { mutableStateOf<Biome?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        Database.fetchBiomes(
            onSuccess = { biomes -> selectedBiome = biomes.find { it.name == biomeId } },
            onFailure = { errorMessage = it.message }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFAA603D))
            .padding(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Enclos de $biomeId",
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
                            EnclosureCard(enclosure, biomeId, navController)
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
fun EnclosureCard(enclosure: Enclosure, biomeId: String, navController: NavHostController) {
    val userId = "User123" // TODO: Récupérer l'ID réel de l'utilisateur connecté
    var rating by remember { mutableStateOf(0) }

    LaunchedEffect(enclosure.id) {
        val ratingRef = FirebaseDatabase.getInstance()
            .getReference("biomes")
            .child(biomeId)
            .child("enclosures")
            .child(enclosure.id)
            .child("ratings")
            .child(userId)

        ratingRef.get().addOnSuccessListener { snapshot ->
            snapshot.getValue(Int::class.java)?.let { savedRating ->
                rating = savedRating
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFD38B))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Enclos ${enclosure.id}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Animaux :", fontWeight = FontWeight.Bold)
            enclosure.animals.forEach { animal ->
                Text(text = "• ${animal.name}")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Notez cet enclos :", fontWeight = FontWeight.Bold)
            RatingBar(currentRating = rating) { selectedRating ->
                rating = selectedRating
                Database.addRating(
                    biomeId, enclosure.id, userId, selectedRating,
                    onSuccess = { println("✅ Note mise à jour") },
                    onFailure = { println("❌ Échec de l'enregistrement") }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { navController.navigate("enclosure_comments/$biomeId/${enclosure.id}") },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Commenter")
            }
        }
    }
}



@Composable
fun RatingBar(
    currentRating: Int,
    onRatingSelected: (Int) -> Unit
) {
    Row {
        (1..5).forEach { star ->
            Icon(
                painter = painterResource(if (star <= currentRating) R.drawable.star_filled else R.drawable.star_empty),
                contentDescription = "Rating star",
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(32.dp)
                    .clickable { onRatingSelected(star) }
            )
        }
    }
}
