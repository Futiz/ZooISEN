package fr.isen.sannicolas.zooisen.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader
import java.util.PriorityQueue

// Définition des données du graphe
typealias Graph = Map<String, Map<String, Double>>

object NavigationManager {
    private var zooGraph: Graph = emptyMap()

    // Charger le graphe depuis zooGraph.json
    fun loadGraph(context: Context) {
        try {
            val inputStream = context.assets.open("zooGraph.json")
            val reader = InputStreamReader(inputStream)
            val type = object : TypeToken<Graph>() {}.type
            zooGraph = Gson().fromJson(reader, type)
            reader.close()
        } catch (e: Exception) {
            println("❌ Erreur lors du chargement du graphe : ${e.message}")
        }
    }

    // Trouver le plus court chemin avec Dijkstra
    fun findShortestPath(start: String, end: String): List<String> {
        if (!zooGraph.containsKey(start) || !zooGraph.containsKey(end)) {
            return listOf("❌ Erreur : '$start' ou '$end' n'existe pas dans le graphe.")
        }

        val distances = mutableMapOf<String, Double>().withDefault { Double.MAX_VALUE }
        val previousNodes = mutableMapOf<String, String?>()
        val priorityQueue = PriorityQueue<Pair<String, Double>>(compareBy { it.second })

        distances[start] = 0.0
        priorityQueue.add(start to 0.0)

        while (priorityQueue.isNotEmpty()) {
            val (currentNode, currentDist) = priorityQueue.poll()

            if (currentNode == end) break

            zooGraph[currentNode]?.forEach { (neighbor, distance) ->
                val newDist = currentDist + distance
                if (newDist < distances.getValue(neighbor)) {
                    distances[neighbor] = newDist
                    previousNodes[neighbor] = currentNode
                    priorityQueue.add(neighbor to newDist)
                }
            }
        }

        return reconstructPath(previousNodes, start, end)
    }

    // Reconstruire le chemin optimal
    private fun reconstructPath(previousNodes: Map<String, String?>, start: String, end: String): List<String> {
        val path = mutableListOf<String>()
        var current: String? = end

        while (current != null) {
            path.add(current)
            current = previousNodes[current]
        }

        return if (path.last() == start) path.reversed() else listOf("❌ Aucun itinéraire trouvé entre $start et $end.")
    }
}
