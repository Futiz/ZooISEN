package fr.isen.sannicolas.zooisen.screens

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

@OptIn(ExperimentalMaterial3Api::class)
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

    Scaffold(
        containerColor = Color(0xFFF5E9D2),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Commentaires - Enclos $enclosureId",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFD4C1A4))
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(comments) { comment ->
                        CommentCard(comment)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = newComment,
                    onValueChange = { newComment = it },
                    label = { Text("Ajouter un commentaire", color = Color.White) },
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White, // ✅ Texte en blanc
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White,
                        focusedContainerColor = Color(0xFF484848), // ✅ Fond plus foncé
                        unfocusedContainerColor = Color(0xFF3C3C3C), // ✅ Fond gris doux
                        focusedIndicatorColor = Color(0xFF1976D2),
                        unfocusedIndicatorColor = Color.Gray
                    ),
                            modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { navController.popBackStack() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF757575)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Retour", color = Color.White)
                    }

                    Button(
                        onClick = {
                            if (newComment.isNotBlank()) {
                                Database.addComment(
                                    biomeId, enclosureId, "Utilisateur", newComment,
                                    onSuccess = { newComment = "" },
                                    onFailure = { println("❌ Échec envoi commentaire") }
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)), // Bleu cohérent
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Envoyer", color = Color.White)
                    }
                }
            }
        }
    )
}

@Composable
fun CommentCard(comment: Comment) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF484848)), // Gris foncé doux
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = comment.author,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1976D2)
            )
            Text(
                text = comment.text,
                fontSize = 14.sp,
                color = Color(0xFFEEEEEE)
            )
        }
    }
}
