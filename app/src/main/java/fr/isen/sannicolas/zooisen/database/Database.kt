package fr.isen.sannicolas.zooisen.database

import com.google.firebase.database.*

data class Animal(
    val id_animal: String = "",
    val name: String = "",
    val id_enclos: String = ""
)

data class Enclosure(
    val id: String = "",
    val animals: List<Animal> = emptyList(),
    val ratings: Map<String, Int>? = null,
    val isOpen: Boolean = true
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
                        val enclosureId = enclosureSnapshot.key.orEmpty()

                        // Récupérer les animaux
                        val animalsList = enclosureSnapshot.child("animals").children.map { animalSnapshot ->
                            val id_animal = animalSnapshot.child("id_animal").getValue(String::class.java) ?: ""
                            val name = animalSnapshot.child("name").getValue(String::class.java) ?: ""
                            val id_enclos = animalSnapshot.child("id_enclos").getValue(String::class.java) ?: ""
                            Animal(id_animal, name, id_enclos)
                        }

                        // Récupérer les évaluations
                        val ratingsType = object : GenericTypeIndicator<Map<String, Int>>() {}
                        val ratings = enclosureSnapshot.child("ratings").getValue(ratingsType) ?: emptyMap()

                        // Statut de l'enclos
                        val isOpen = enclosureSnapshot.child("isOpen").getValue(Boolean::class.java) ?: true

                        val enclosure = Enclosure(
                            id = enclosureId,
                            animals = animalsList,
                            ratings = ratings,
                            isOpen = isOpen
                        )

                        enclosuresList.add(enclosure)
                    }

                    val biome = Biome(
                        color = color,
                        name = name,
                        enclosures = enclosuresList
                    )
                    biomeList.add(biome)
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

    fun addRating(
        biomeId: String,
        enclosureId: String,
        userId: String,
        rating: Int,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val ratingsRef = biomesRef
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
        val ratingsRef = biomesRef
            .child(biomeId)
            .child("enclosures")
            .child(enclosureId)
            .child("ratings")

        ratingsRef.get().addOnSuccessListener { snapshot ->
            val ratings = snapshot.children.mapNotNull { it.getValue(Int::class.java) }
            val averageRating = if (ratings.isNotEmpty()) ratings.average() else 0.0

            biomesRef
                .child(biomeId)
                .child("enclosures")
                .child(enclosureId)
                .child("averageRating")
                .setValue(averageRating)
        }
    }

    fun initializeEnclosureOpenStatus() {
        val database = FirebaseDatabase.getInstance().reference.child("biomes")
        database.get().addOnSuccessListener { snapshot ->
            snapshot.children.forEach { biomeSnapshot ->
                val biomeName = biomeSnapshot.key.orEmpty()
                biomeSnapshot.child("enclosures").children.forEach { enclosureSnapshot ->
                    val enclosureRef = database.child(biomeName).child("enclosures").child(enclosureSnapshot.key.orEmpty())
                    enclosureRef.child("isOpen").setValue(true)
                }
            }
            println("✅ Mise à jour des enclos terminée !")
        }.addOnFailureListener {
            println("❌ Erreur lors de la mise à jour : ${it.message}")
        }
    }

    fun updateEnclosureStatus(
        biomeName: String,
        enclosureId: String,
        isOpen: Boolean,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        val enclosureRef = biomesRef
            .child(biomeName)
            .child("enclosures")
            .child(enclosureId)
            .child("isOpen")

        enclosureRef.setValue(isOpen)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
    }
}
