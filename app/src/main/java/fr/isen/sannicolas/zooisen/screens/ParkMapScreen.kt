package fr.isen.sannicolas.zooisen.screens

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import fr.isen.sannicolas.zooisen.R

@Composable
fun ParkMapScreen(navController: NavHostController) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Plan du Parc",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Image(
                painter = painterResource(id = R.drawable.plan),
                contentDescription = "Plan du Parc",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val intent = Intent(context, FullscreenMapActivity::class.java)
                        context.startActivity(intent)
                    }
            )
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
