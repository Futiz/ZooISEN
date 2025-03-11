package fr.isen.sannicolas.zooisen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import fr.isen.sannicolas.zooisen.screens.BiomeScreen
import fr.isen.sannicolas.zooisen.screens.EnclosureScreen
import fr.isen.sannicolas.zooisen.ui.theme.ZooISENTheme
import fr.isen.sannicolas.zooisen.screens.EnclosureCommentScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ZooISENTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "biomes",
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable("biomes") {
                        BiomeScreen(navController)
                    }
                    composable("enclosures/{biomeName}") { backStackEntry ->
                        val biomeName = backStackEntry.arguments?.getString("biomeName") ?: ""
                        EnclosureScreen(navController, biomeName) // ✅ Affiche les enclos d'un biome
                    }
                    composable("enclosure_comments/{biomeName}/{enclosure.id}") { backStackEntry ->
                        val biomeName = backStackEntry.arguments?.getString("biomeName") ?: ""
                        val enclosureId = backStackEntry.arguments?.getString("enclosureId") ?: ""
                        EnclosureCommentScreen(navController, enclosureId) //
                    }
                }
            }
        }
    }
}
