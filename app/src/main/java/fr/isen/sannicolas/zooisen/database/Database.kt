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
    val id: String = "",
    val author: String = "",
    val text: String = ""
)

data class Rating(
    val userId: String = "",
    val rating: Int = 0
)


object Database {
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val biomesRef: DatabaseReference = database.getReference("biomes")

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
        biomeId: String,
        enclosureId: String,
        onSuccess: (List<Comment>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val commentsRef = biomesRef.child(biomeId).child("enclosures").child(enclosureId).child("comments")

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

    fun addComment(
        biomeId: String,
        enclosureId: String,
        author: String,
        text: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val commentsRef = biomesRef.child(biomeId).child("enclosures").child(enclosureId).child("comments")
        val newCommentRef = commentsRef.push()

        val comment = Comment(
            id = newCommentRef.key ?: "",
            author = author,
            text = text
        )

        newCommentRef.setValue(comment)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun addRating(biomeId: String, enclosureId: String, userId: String, rating: Int, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val ratingsRef = FirebaseDatabase.getInstance().getReference("biomes")
            .child(biomeId)
            .child("enclosures")
            .child(enclosureId)
            .child("ratings")
            .child(userId)

        ratingsRef.setValue(rating)
            .addOnSuccessListener {
                updateAverageRating(biomeId, enclosureId)
                onSuccess()
            }
            .addOnFailureListener { onFailure(it) }
    }

    fun updateAverageRating(biomeId: String, enclosureId: String) {
        val ratingsRef = FirebaseDatabase.getInstance().getReference("biomes")
            .child(biomeId)
            .child("enclosures")
            .child(enclosureId)
            .child("ratings")

        ratingsRef.get().addOnSuccessListener { snapshot ->
            val ratings = snapshot.children.mapNotNull { it.getValue(Int::class.java) }
            val averageRating = if (ratings.isNotEmpty()) ratings.average() else 0.0

            FirebaseDatabase.getInstance().getReference("biomes")
                .child(biomeId)
                .child("enclosures")
                .child(enclosureId)
                .child("averageRating")
                .setValue(averageRating)
        }
    }

}
