package fr.isen.sannicolas.zooisen.screens

import android.os.Bundle
import android.view.View
import android.content.pm.ActivityInfo
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import fr.isen.sannicolas.zooisen.R

class FullscreenMapActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        window?.decorView?.let { decorView ->
            decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }

        setContent {
            FullscreenMapScreen { finish() }
        }
    }
}

@Composable
fun FullscreenMapScreen(onClose: () -> Unit) {
    var scale by remember { mutableStateOf(1f) }
    val transformableState = rememberTransformableState { zoomChange, _, _ ->
        scale = (scale * zoomChange).coerceIn(1f, 3f)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Carte zoomable et scrollable
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
                    .graphicsLayer(scaleX = scale, scaleY = scale)
                    .transformable(transformableState)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bouton retour
        Button(
            onClick = { onClose() },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF005BBB))
        ) {
            Text("Fermer", color = Color.White)
        }
    }
}
