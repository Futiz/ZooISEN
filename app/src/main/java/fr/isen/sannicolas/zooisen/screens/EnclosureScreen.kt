package fr.isen.sannicolas.zooisen.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import fr.isen.sannicolas.zooisen.database.Biome
import fr.isen.sannicolas.zooisen.database.Database
import fr.isen.sannicolas.zooisen.database.Enclosure
import fr.isen.sannicolas.zooisen.R
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
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

    Scaffold(
        containerColor = Color(0xFFF5E9D2),
        topBar = {
            TopAppBar(
                title = { Text("Enclos de $biomeId", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFD4C1A4))
            )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (errorMessage != null) {
                        Text(
                            text = "Erreur : $errorMessage",
                            color = Color.Red,
                            modifier = Modifier.padding(8.dp)
                        )
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
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
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)), // Bleu plus doux
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Retour aux Biomes", color = Color.White)
                    }
                }
            }
        }
    )
}

@Composable
fun EnclosureCard(enclosure: Enclosure, biomeId: String, navController: NavHostController) {
    val userId = "User123" // TODO: Récupérer l'ID réel de l'utilisateur connecté
    var rating by remember { mutableIntStateOf(0) }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

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
            .padding(12.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF484848)) // Gris doux au lieu du trop foncé
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Enclos ${enclosure.id}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(text = "Animaux :", fontWeight = FontWeight.Bold, color = Color.White)
            enclosure.animals.forEach { animal ->
                Text(text = "• ${animal.name}", color = Color(0xFFEEEEEE))
            }

            Text(text = "Notez cet enclos :", fontWeight = FontWeight.Bold, color = Color.White)

            RatingBar(currentRating = rating) { selectedRating ->
                rating = selectedRating
                Database.addRating(
                    biomeId, enclosure.id, userId, selectedRating,
                    onSuccess = {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("✅ Note mise à jour")
                        }
                    },
                    onFailure = {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("❌ Échec de l'enregistrement")
                        }
                    }
                )
            }

            Button(
                onClick = { navController.navigate("enclosure_comments/$biomeId/${enclosure.id}") },
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Commenter", color = Color.White)
            }
        }
    }
}

@Composable
fun RatingBar(
    currentRating: Int,
    onRatingSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        (1..5).forEach { star ->
            val starIcon = if (star <= currentRating) {
                painterResource(id = R.drawable.star)
            } else {
                painterResource(id = R.drawable.star_empty)
            }

            Icon(
                painter = starIcon,
                contentDescription = "Rating star",
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(32.dp)
                    .clickable { onRatingSelected(star) }
            )
        }
    }
}

