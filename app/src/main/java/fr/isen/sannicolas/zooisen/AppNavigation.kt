package fr.isen.sannicolas.zooisen.screens

import AuthScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fr.isen.sannicolas.zooisen.screens.RegisterScreen

@Composable
fun AppNavigation(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = "auth",
        modifier = modifier
    ) {
        composable(route = "auth") { AuthScreen(navController) }
        composable(route = "register") { RegisterScreen(navController) }
    }
}
