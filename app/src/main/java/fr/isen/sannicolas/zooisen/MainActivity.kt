package fr.isen.sannicolas.zooisen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import fr.isen.sannicolas.zooisen.screens.BiomeScreen
import fr.isen.sannicolas.zooisen.screens.EnclosureScreen
import fr.isen.sannicolas.zooisen.screens.ParkServiceScreen
import fr.isen.sannicolas.zooisen.screens.EnclosureCommentScreen
import fr.isen.sannicolas.zooisen.screens.ParkMapScreen
import fr.isen.sannicolas.zooisen.ui.theme.ZooISENTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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

                    composable("park_services") {
                        ParkServiceScreen(navController)
                    }

                    composable("enclosures/{biomeId}") { backStackEntry ->
                        val biomeId = backStackEntry.arguments?.getString("biomeId") ?: ""
                        EnclosureScreen(navController, biomeId)
                    }

                    composable("enclosure_comments/{biomeId}/{enclosureId}") { backStackEntry ->
                        val biomeId = backStackEntry.arguments?.getString("biomeId") ?: ""
                        val enclosureId = backStackEntry.arguments?.getString("enclosureId") ?: ""
                        EnclosureCommentScreen(navController, biomeId, enclosureId)
                    }
                    composable("park_map") {
                        ParkMapScreen(navController)
                    }
                }
            }
        }
    }
}

