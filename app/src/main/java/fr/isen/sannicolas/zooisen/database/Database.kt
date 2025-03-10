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
    val meal: String = "",
    var animals: List<Animal> = emptyList() // 🔥 Remplacé Map par List
)

data class Biome(
    val color: String = "",
    val name: String = "",
    var enclosures: List<Enclosure> = emptyList() // 🔥 Remplacé Map par List
)

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

                        enclosuresList.add(Enclosure(id_biomes, meal, animalsList))
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
}
