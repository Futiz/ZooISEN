package fr.isen.sannicolas.zooisen.navigation

import kotlin.math.sqrt

object NavigationManager {

    private val zooGraph = mapOf(
        "Enclos 1" to listOf("Gare" to 5.0, "Restaurant" to 8.0),
        "Gare" to listOf("Enclos 1" to 5.0, "Enclos 2" to 4.0, "Boutique" to 7.0),
        "Restaurant" to listOf("Enclos 1" to 8.0, "Boutique" to 3.0),
        "Boutique" to listOf("Gare" to 7.0, "Restaurant" to 3.0, "Enclos 3" to 6.0),
    )

    private val positions = mapOf(
        "Enclos 1" to Pair(100.0, 200.0),
        "Gare" to Pair(150.0, 180.0),
        "Restaurant" to Pair(90.0, 250.0),
        "Boutique" to Pair(120.0, 220.0),
    )

    private fun heuristic(node: String, destination: String): Double {
        val (x1, y1) = positions[node] ?: return Double.MAX_VALUE
        val (x2, y2) = positions[destination] ?: return Double.MAX_VALUE
        return sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1))
    }

    fun findShortestPath(start: String, goal: String): List<String> {
        val openSet = mutableListOf(Node(start, 0.0, heuristic(start, goal)))
        val cameFrom = mutableMapOf<String, String>()
        val gScore = mutableMapOf(start to 0.0)

        while (openSet.isNotEmpty()) {
            openSet.sortBy { it.cost + it.heuristic }
            val current = openSet.removeAt(0)

            if (current.name == goal) {
                val path = mutableListOf(goal)
                var step = goal
                while (cameFrom.containsKey(step)) {
                    step = cameFrom[step]!!
                    path.add(0, step)
                }
                return path
            }

            for ((neighbor, weight) in zooGraph[current.name] ?: emptyList()) {
                val tentativeGScore = gScore[current.name]!! + weight

                if (tentativeGScore < gScore.getOrDefault(neighbor, Double.MAX_VALUE)) {
                    cameFrom[neighbor] = current.name
                    gScore[neighbor] = tentativeGScore
                    openSet.add(Node(neighbor, tentativeGScore, heuristic(neighbor, goal)))
                }
            }
        }
        return emptyList()
    }

    private data class Node(val name: String, val cost: Double, val heuristic: Double)
}
