package fr.isen.sannicolas.zooisen.database

import android.util.Log
import com.google.firebase.database.*

// Modèle de données pour correspondre à la base Firebase
data class Animal(
    val id_animal: String = "",
    val name: String = "",
    val id_enclos: String = ""
)

data class Enclosure(
    val id_biomes: String = "",
    val id: String = "",
    var animals: List<Animal> = emptyList() // 🔥 Remplacé Map par List
)

data class Biome(
    val color: String = "",
    val name: String = "",
    var enclosures: List<Enclosure> = emptyList() // 🔥 Remplacé Map par List
)

data class Comment(val author: String = "", val text: String = "")

object Database {
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val biomesRef: DatabaseReference = database.getReference("biomes")

    // 🔹 Lire les biomes depuis Firebase
    fun fetchBiomes(onSuccess: (List<Biome>) -> Unit, onFailure: (Exception) -> Unit) {
        biomesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val biomeList = mutableListOf<Biome>()

                for (biomeSnapshot in snapshot.children) {
                    val color = biomeSnapshot.child("color").getValue(String::class.java) ?: ""
                    val name = biomeSnapshot.child("name").getValue(String::class.java) ?: ""

                    val enclosuresList = mutableListOf<Enclosure>()
                    val enclosuresSnapshot = biomeSnapshot.child("enclosures")

                    for (enclosureSnapshot in enclosuresSnapshot.children) {
                        val id = enclosureSnapshot.key ?: ""
                        val id_biomes = enclosureSnapshot.child("id_biomes").getValue(String::class.java) ?: ""
                        val meal = enclosureSnapshot.child("meal").getValue(String::class.java) ?: ""

                        val animalsList = mutableListOf<Animal>()
                        val animalsSnapshot = enclosureSnapshot.child("animals")

                        for (animalSnapshot in animalsSnapshot.children) {
                            val id_animal = animalSnapshot.child("id_animal").getValue(String::class.java) ?: ""
                            val name = animalSnapshot.child("name").getValue(String::class.java) ?: ""
                            val id_enclos = animalSnapshot.child("id_enclos").getValue(String::class.java) ?: ""

                            animalsList.add(Animal(id_animal, name, id_enclos))
                        }

                        enclosuresList.add(Enclosure(id_biomes, id, animalsList))
                    }

                    biomeList.add(Biome(color, name, enclosuresList))
                }

                onSuccess(biomeList)
            }

            override fun onCancelled(error: DatabaseError) {
                onFailure(error.toException())
            }
        })
    }
    fun fetchComments(enclosureId: String, onSuccess: (List<Comment>) -> Unit, onFailure: (Exception) -> Unit) {
        val commentsRef = biomesRef.child("enclosures").child(enclosureId).child("comments")

        commentsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val commentsList = mutableListOf<Comment>()
                for (commentSnapshot in snapshot.children) {
                    val comment = commentSnapshot.getValue(Comment::class.java)
                    comment?.let { commentsList.add(it) }
                }
                onSuccess(commentsList)
            }

            override fun onCancelled(error: DatabaseError) {
                onFailure(error.toException())
            }
        })
    }

    // 🔹 Ajouter un commentaire à un enclos
    fun addComment(enclosureId: String, author: String, text: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val commentsRef = biomesRef.child("enclosures").child(enclosureId).child("comments")
        val newCommentRef = commentsRef.push()
        val comment = Comment(author, text)

        newCommentRef.setValue(comment)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }
    fun addCommentsSectionToEnclosures() {
        val database = FirebaseDatabase.getInstance()
        val biomesRef = database.getReference("biomes")

        biomesRef.get().addOnSuccessListener { snapshot ->
            snapshot.children.forEach { biomeSnapshot ->
                val enclosuresRef = biomeSnapshot.child("enclosures").ref

                biomeSnapshot.child("enclosures").children.forEach { enclosureSnapshot ->
                    val enclosureId = enclosureSnapshot.key
                    if (enclosureId != null) {
                        // Ajoute la section 'comments' si elle n'existe pas
                        enclosuresRef.child(enclosureId).child("comments").get()
                            .addOnSuccessListener { commentSnapshot ->
                                if (!commentSnapshot.exists()) {
                                    enclosuresRef.child(enclosureId).child("comments").setValue(emptyMap<String, String>())
                                }
                            }
                    }
                }
            }
        }.addOnFailureListener { exception ->
            Log.e("Firebase", "Erreur lors de l'ajout des comments", exception)
        }
    }


}
