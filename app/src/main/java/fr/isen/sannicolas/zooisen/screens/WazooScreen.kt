package fr.isen.sannicolas.zooisen.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import fr.isen.sannicolas.zooisen.utils.NavigationManager
import androidx.compose.ui.graphics.Color

@Composable
fun WazooScreen(navController: NavHostController) {
    var startPoint by remember { mutableStateOf("") }
    var endPoint by remember { mutableStateOf("") }
    var route by remember { mutableStateOf<List<String>?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Planifiez votre itinéraire 🚀",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 🔹 Sélection du point de départ
        OutlinedTextField(
            value = startPoint,
            onValueChange = { startPoint = it },
            label = { Text("Point de départ") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 🔹 Sélection du point d’arrivée
        OutlinedTextField(
            value = endPoint,
            onValueChange = { endPoint = it },
            label = { Text("Point d'arrivée") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 🔹 Bouton pour calculer l'itinéraire
        Button(
            onClick = {
                route = NavigationManager.findShortestPath(startPoint, endPoint)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = startPoint.isNotBlank() && endPoint.isNotBlank()
        ) {
            Text("Calculer l'itinéraire")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 🔹 Affichage du chemin
        route?.let { path ->
            Text("Itinéraire trouvé :")
            path.forEach { step ->
                Text("➡ $step")
            }
            Text(
                text = if (path.isNotEmpty()) path.joinToString(" → ") else "Aucun itinéraire trouvé.",
                color = Color.Black,
                modifier = Modifier.padding(16.dp)
            )

        }

        Spacer(modifier = Modifier.height(16.dp))

        // 🔹 Bouton retour
        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF005BBB))
        ) {
            Text("Retour", color = Color.White)
        }
    }
}
