package fr.isen.sannicolas.zooisen.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import fr.isen.sannicolas.zooisen.database.Comment
import fr.isen.sannicolas.zooisen.database.Database

@Composable
fun EnclosureCommentScreen(navController: NavHostController, biomeId: String, enclosureId: String) {
    var comments by remember { mutableStateOf<List<Comment>>(emptyList()) }
    var newComment by remember { mutableStateOf("") }

    LaunchedEffect(enclosureId) {
        Database.fetchComments(
            biomeId = biomeId,
            enclosureId = enclosureId,
            onSuccess = { comments = it },
            onFailure = { println("❌ Erreur chargement commentaires: ${it.message}") }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            Text(
                text = "Commentaires pour l'Enclos $enclosureId",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth()
            ) {
                items(comments) { comment ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = comment.author,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF007AFF)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = comment.text, fontSize = 14.sp, color = Color(0xFF555555))
                        }
                    }
                }
            }

            OutlinedTextField(
                value = newComment,
                onValueChange = { newComment = it },
                label = { Text("Ajouter un commentaire") },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = { navController.popBackStack() }) { Text("Retour") }
                Button(onClick = { if (newComment.isNotBlank()) Database.addComment(biomeId, enclosureId, "Utilisateur", newComment, { newComment = "" }, {}) }) { Text("Envoyer") }
            }
        }
    }
}
