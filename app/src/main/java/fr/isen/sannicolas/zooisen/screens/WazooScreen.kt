package fr.isen.sannicolas.zooisen.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import fr.isen.sannicolas.zooisen.R
import androidx.compose.ui.draw.clip
import fr.isen.sannicolas.zooisen.waze.NavigationManager
import androidx.compose.foundation.shape.CircleShape

@Composable
fun WazooScreen(navController: NavHostController) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        NavigationManager.loadGraph(context)
    }

    var startPoint by remember { mutableStateOf("") }
    var endPoint by remember { mutableStateOf("") }
    var route by remember { mutableStateOf<List<String>?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val backgroundColor = Color(0xFFF5E9D2)
    val buttonColor = Color(0xFFA35632)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.parc),
            contentDescription = "Logo du parc",
            modifier = Modifier
                .size(180.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Planifiez votre itinéraire 🚀",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = startPoint,
            onValueChange = { startPoint = it },
            label = { Text("Point de départ") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = endPoint,
            onValueChange = { endPoint = it },
            label = { Text("Point d'arrivée") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                errorMessage = null
                val path = NavigationManager.findShortestPathAStar(startPoint, endPoint)
                if (path.size == 1 && path.first().contains("❌")) {
                    errorMessage = "⚠ Erreur : Le chemin entre '$startPoint' et '$endPoint' n'existe pas."
                    route = null
                } else {
                    route = path
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = startPoint.isNotBlank() && endPoint.isNotBlank(),
            colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
        ) {
            Text("Calculer l'itinéraire", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        errorMessage?.let {
            Text(
                text = it,
                color = Color.Red,
                modifier = Modifier.padding(8.dp)
            )
        }

        route?.let { path ->
            Text("Itinéraire trouvé :")
            path.forEach { step ->
                Text("➡ $step")
            }
            Text(
                text = path.joinToString(" → "),
                color = Color.Black,
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 🔹 Zone de texte explicative
        Text(
            text = "Voici un outil permettant de calculer un itinéraire entre des enclos et des services du parc.\n\n" +
                    "Pour cela, veuillez renseigner \"Enclos X\" ou bien le nom du service du parc.",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.DarkGray,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
        ) {
            Text("Retour", color = Color.White)
        }
    }
}

