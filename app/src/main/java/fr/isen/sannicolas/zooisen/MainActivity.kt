package fr.isen.sannicolas.zooisen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import fr.isen.sannicolas.zooisen.ui.theme.ZooISENTheme
import androidx.navigation.compose.rememberNavController
import fr.isen.sannicolas.zooisen.screens.AppNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ZooISENTheme {
                val navController = rememberNavController() // Initialisation du NavController
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(navController, modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
