package fr.isen.sannicolas.zooisen.waze

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader
import java.util.PriorityQueue

typealias Graph = Map<String, Map<String, Double>>

object NavigationManager {
    private var zooGraph: Graph = emptyMap()

    // Charger le graphe
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

    // Algorithme A* (A Star)
    fun findShortestPathAStar(start: String, end: String): List<String> {
        if (!zooGraph.containsKey(start) || !zooGraph.containsKey(end)) {
            return listOf("❌ Erreur : '$start' ou '$end' n'existe pas dans le graphe.")
        }

        val distances = mutableMapOf<String, Double>().withDefault { Double.MAX_VALUE }
        val estimatedTotalCost = mutableMapOf<String, Double>().withDefault { Double.MAX_VALUE }
        val previousNodes = mutableMapOf<String, String?>()
        val priorityQueue = PriorityQueue<Pair<String, Double>>(compareBy { it.second })

        distances[start] = 0.0
        estimatedTotalCost[start] = heuristic(start, end)
        priorityQueue.add(start to estimatedTotalCost[start]!!)

        while (priorityQueue.isNotEmpty()) {
            val (currentNode, _) = priorityQueue.poll()

            if (currentNode == end) break

            zooGraph[currentNode]?.forEach { (neighbor, distance) ->
                val newDist = distances.getValue(currentNode) + distance
                if (newDist < distances.getValue(neighbor)) {
                    distances[neighbor] = newDist
                    estimatedTotalCost[neighbor] = newDist + heuristic(neighbor, end)
                    previousNodes[neighbor] = currentNode
                    priorityQueue.add(neighbor to estimatedTotalCost[neighbor]!!)
                }
            }
        }

        return reconstructPath(previousNodes, start, end)
    }

    // Heuristique : estimation basée sur la distance minimale d’une arête
    private fun heuristic(node: String, target: String): Double {
        return zooGraph[node]?.values?.minOrNull() ?: 1.0 // Distance minimale connue vers un voisin
    }

    // Reconstruire le chemin
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
