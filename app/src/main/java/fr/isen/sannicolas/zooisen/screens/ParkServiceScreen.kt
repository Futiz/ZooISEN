package fr.isen.sannicolas.zooisen.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.clickable
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import fr.isen.sannicolas.zooisen.R

data class ParkService(
    val name: String,
    val icon: Int,
    val location: String
)

@Composable
fun ParkServiceScreen(navController: NavHostController) {
    val parkServices = listOf(
        ParkService("Toilettes", R.drawable.toilettes, "La Bergerie des Reptiles, le Plateau et les Clairières"),
        ParkService("Point d'eau", R.drawable.pointdeau, "Le Plateau, le Vallon des cascades et les Clairières"),
        ParkService("Boutique", R.drawable.boutique, "À l'entrée du parc"),
        ParkService("Gare", R.drawable.gare, "Dans le biome 'Le Plateau' et à l'entrée du Parc"),
        ParkService("Lodge", R.drawable.lodge, "Dans le biome 'Le Plateau'"),
        ParkService("Tente pédagogique", R.drawable.tente, "Dans le biome 'Le Plateau'"),
        ParkService("Paillote", R.drawable.paillote, "Proche du restaurant du parc et dans le biome 'Le Plateau'"),
        ParkService("Café nomade", R.drawable.cafe, "Dans le biome 'le Vallon des Cascades'"),
        ParkService("Petit Café", R.drawable.petitcafe, "À côté de l'entrée de la Bergerie des reptiles"),
        ParkService("Plateau des jeux", R.drawable.plateau, "Dans le biome 'Le Plateau'"),
        ParkService("Espace Pique-nique", R.drawable.piquenique, "Présent dans plusieurs zones"),
        ParkService("Point de vue", R.drawable.pointdevue, "Situé dans 'Le Belvédère', 'les Clairières' et 'Le Plateau'"),
        ParkService("Sortie de secours", R.drawable.sortiesecours, "Entrée du parc, le Vallon des cascades, le Bois des pins et les Clairières"),
        ParkService("Poste de secours", R.drawable.postedesecours, "À l'entrée du parc"),
        ParkService("Point de rassemblement", R.drawable.pointrassemblement, "Entrée du parc, le Vallon des cascades, le Plateau et les Clairières")
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Services du Parc",
                fontSize = 24.sp,
                color = Color(0xFF333333)
            )
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(parkServices) { service ->
                    ServiceCard(service)
                }

                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable { navController.navigate("park_map") },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2196F3))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.plan),
                                contentDescription = "Plan du Parc",
                                modifier = Modifier
                                    .size(60.dp)
                                    .padding(end = 16.dp),
                                contentScale = ContentScale.Fit
                            )

                            Column {
                                Text(
                                    text = "Plan du Parc",
                                    fontSize = 18.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = "Accédez à la carte interactive",
                                    fontSize = 14.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF005BBB))
            ) {
                Text("Retour", color = Color.White)
            }
        }
    }
}

@Composable
fun ServiceCard(service: ParkService) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = service.icon),
                contentDescription = service.name,
                modifier = Modifier
                    .size(40.dp)
                    .padding(end = 16.dp),
                contentScale = ContentScale.Fit
            )

            Column {
                Text(
                    text = service.name,
                    fontSize = 18.sp,
                    color = Color.Black
                )
                Text(
                    text = service.location,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
