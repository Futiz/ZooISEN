package fr.isen.sannicolas.zooisen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.firebase.database.FirebaseDatabase
import fr.isen.sannicolas.zooisen.screens.*
import fr.isen.sannicolas.zooisen.ui.theme.ZooISENTheme
import java.io.InputStream

data class Animal(val id: String, val name: String, val id_enclos: String, val id_animal: String)
data class Enclosure(val id: String, val id_biomes: String, val meal: String, val animals: List<Animal>)
data class Biome(val id: String, val color: String, val name: String, val enclosures: List<Enclosure>)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        setContent {
            ZooISENTheme {
                val navController = rememberNavController()

                LaunchedEffect(Unit) {
                    //importZooData()
                }

                NavHost(
                    navController = navController,
                    startDestination = "auth",
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable("auth") {
                        AuthScreen(navController)
                    }
                    composable("register") {
                        RegisterScreen(navController)
                    }
                    composable("create") {
                        CreateAccountScreen(navController)
                    }
                    composable("biomes") {
                        BiomeScreen(navController)
                    }
                    composable("park_services") {
                        ParkServiceScreen(navController)
                    }
                    composable("enclosures/{biomeId}") { backStackEntry ->
                        val biomeId = backStackEntry.arguments?.getString("biomeId") ?: ""
                        EnclosureScreen(navController, biomeId)
                    }
                    composable("enclosure_comments/{biomeId}/{enclosureId}") { backStackEntry ->
                        val biomeId = backStackEntry.arguments?.getString("biomeId") ?: ""
                        val enclosureId = backStackEntry.arguments?.getString("enclosureId") ?: ""
                        EnclosureCommentScreen(navController, biomeId, enclosureId)
                    }
                    composable("admin_enclosures") {  // Cette ligne doit exister
                        AdminEnclosureScreen(navController)  // Assurez-vous que l'écran est bien là
                    }
                    composable("park_map") {
                        ParkMapScreen(navController)
                    }
                }
            }
        }
    }

    private fun importZooData() {
        try {
            val inputStream: InputStream = assets.open("zoo.json")
            val jsonString = inputStream.bufferedReader().use { it.readText() }

            val biomesType = object : TypeToken<List<Biome>>() {}.type
            val biomes: List<Biome> = Gson().fromJson(jsonString, biomesType)

            val database = FirebaseDatabase.getInstance().reference

            biomes.forEach { biome ->
                val biomeRef = database.child("biomes").child(biome.name)
                biomeRef.child("id").setValue(biome.id)
                biomeRef.child("color").setValue(biome.color)
                biomeRef.child("name").setValue(biome.name)

                biome.enclosures.forEach { enclosure ->
                    val enclosureRef = biomeRef.child("enclosures").child(enclosure.id)
                    enclosureRef.child("id").setValue(enclosure.id)
                    enclosureRef.child("meal").setValue(enclosure.meal)
                    enclosureRef.child("isOpen").setValue(true) // ✅ Champ auto ajouté !

                    enclosure.animals.forEach { animal ->
                        val animalRef = enclosureRef.child("animals").child(animal.id)
                        animalRef.child("id").setValue(animal.id)
                        animalRef.child("name").setValue(animal.name)
                        animalRef.child("id_enclos").setValue(animal.id_enclos)
                        animalRef.child("id_animal").setValue(animal.id_animal)
                    }
                }
            }

            println("✅ Importation des données terminée avec succès !")

        } catch (e: Exception) {
            e.printStackTrace()
            println("❌ Erreur lors de l'import des données : ${e.message}")
        }
    }
}
