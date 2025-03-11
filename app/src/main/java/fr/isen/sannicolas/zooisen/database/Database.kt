package fr.isen.sannicolas.zooisen.database

import com.google.firebase.database.*

data class Animal(
    val id_animal: String = "",
    val name: String = "",
    val id_enclos: String = ""
)

data class Enclosure(
    val id_biomes: String = "",
    val id: String = "",
    var animals: List<Animal> = emptyList()
)

data class Biome(
    val color: String = "",
    val name: String = "",
    var enclosures: List<Enclosure> = emptyList()
)

data class Comment(
    val id: String = "", // ✅ ID DU COMMENTAIRE
    val author: String = "",
    val text: String = "",
    val enclosureId: String = "" // ✅ ID DE L'ENCLOS
)

object Database {
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val enclosuresRef: DatabaseReference = database.getReference("enclosures")

    fun fetchBiomes(onSuccess: (List<Biome>) -> Unit, onFailure: (Exception) -> Unit) {
        val biomesRef = database.getReference("biomes")

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

    fun fetchComments(
        enclosureId: String,
        onSuccess: (List<Comment>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val commentsRef = enclosuresRef.child(enclosureId).child("comments")

        commentsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val commentsList = mutableListOf<Comment>()
                for (commentSnapshot in snapshot.children) {
                    val commentId = commentSnapshot.key ?: ""
                    val comment = commentSnapshot.getValue(Comment::class.java)
                    comment?.let {
                        commentsList.add(it.copy(id = commentId, enclosureId = enclosureId)) // ✅ On stocke bien l'ID de l'enclos
                    }
                }
                onSuccess(commentsList)
            }

            override fun onCancelled(error: DatabaseError) {
                onFailure(error.toException())
            }
        })
    }

    fun addComment(
        enclosureId: String,
        author: String,
        text: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val commentsRef = enclosuresRef.child(enclosureId).child("comments")
        val newCommentRef = commentsRef.push()

        val comment = Comment(
            id = newCommentRef.key ?: "", // ✅ ID DU COMMENTAIRE
            author = author,
            text = text,
            enclosureId = enclosureId // ✅ ON STOCKE BIEN L'ID DE L'ENCLOS !
        )

        newCommentRef.setValue(comment)
            .addOnSuccessListener {
                println("✅ Commentaire ajouté SOUS l'enclos : $enclosureId")
                onSuccess()
            }
            .addOnFailureListener {
                println("❌ Erreur ajout commentaire : ${it.message}")
                onFailure(it)
            }
    }
}
