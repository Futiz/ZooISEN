package fr.isen.sannicolas.zooisen

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import fr.isen.sannicolas.zooisen.database.Biome
import fr.isen.sannicolas.zooisen.database.Database
import fr.isen.sannicolas.zooisen.ui.theme.ZooISENTheme
import androidx.compose.ui.unit.dp


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ZooISENTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    BiomeScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun BiomeScreen(modifier: Modifier = Modifier) {
    var biomes by remember { mutableStateOf<List<Biome>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        Database.fetchBiomes(
            onSuccess = { biomes = it },
            onFailure = { errorMessage = it.message }
        )
    }

    Column(modifier = modifier.padding(16.dp)) {
        Text(text = "Liste des Biomes :", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))

        if (errorMessage != null) {
            Text(text = "Erreur : $errorMessage", color = MaterialTheme.colorScheme.error)
        } else {
            biomes.forEach { biome ->
                Text(text = "- ${biome.name}", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}


