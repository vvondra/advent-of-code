import java.io.File
import java.util.*
import kotlin.math.max
import kotlin.math.min
import kotlin.math.ceil

val blueprints = File("input/19.in")
    .readLines()
    .map { Blueprint.of(it) }
    .associate { it.id to it }

data class Blueprint(val id: Int, val recipes: Map<String, Map<String, Int>>) {
    fun ratio() = mapOf(
            //"ore" to recipes["obsidian"]!!["ore"]!! + recipes["clay"]!!["ore"]!! + recipes["ore"]!!["ore"]!!,
            "clay" to recipes["obsidian"]!!["clay"]!!,
            "obsidian" to recipes["geode"]!!["obsidian"]!!
         )
        .map { (k, v) -> k to v }
        .sortedByDescending { it.second }
        .map { it.first }

    val caps: Map<String, Int> = recipes.entries.map { it.value }.reduce { a, b ->
        a.toMutableMap().apply {
            b.forEach { key, value -> merge(key, value) { a, b -> max(a, b )} }
        }.toMap()
    }

    companion object {
        fun of(line: String): Blueprint {
            val (print, rest) = line.split(":").map(String::trim)
            val id = print.split(" ").last().toInt()
            val recipes = rest.split(".").dropLast(1)
                .associate { ingredient ->
                    val (resourcePart, ingredientList) = ingredient.split(" robot costs ").map(String::trim)
                    val parts = ingredientList.split(" and ").associate { part ->
                        part.split(" ").let { it[1] to it[0].toInt() }
                    }

                    resourcePart.split(" ").last() to parts
                }

            return Blueprint(id, recipes)
        }
    }
}

data class State(
    val blueprint: Blueprint,
    val resources: Map<String, Int>,
    val robots: Map<String, Int>,
    val minute: Int,
    val maxTime: Int
) {
    val done = minute == maxTime
    val remainingTime = maxTime - minute
    fun resource(name: String) = resources.getOrDefault(name, 0)
    fun robot(name: String) = robots.getOrDefault(name, 0)

    fun options(): Iterable<State> = mutableListOf<State>().apply {
        listOf("ore", "clay", "obsidian", "geode").forEach { material ->
            val recipe = blueprint.recipes[material]!!

            if (recipe.any { ingredient -> robot(ingredient.key) == 0 && resource(ingredient.key) < ingredient.value }) return@forEach
            if (material != "geode") {
                if (robot(material) * remainingTime + resource(material) >= remainingTime * blueprint.caps[material]!!) return@forEach
                if (robot(material) >= blueprint.caps[material]!!) return@forEach
            }

            val minutes =  max(0, recipe.entries.maxOf { ingredient ->
                ceil((ingredient.value - resource(ingredient.key)) / robot(ingredient.key).toFloat())
            }.toInt()) + 1

            if (minutes > remainingTime) return@forEach

            add(
                copy(
                    resources = resources.mapValues {
                        it.value - recipe.getOrDefault(it.key, 0) + (robots.getOrDefault(it.key, 0) * minutes)
                    },
                    robots = robots + (material to robots.getOrDefault(material, 0) + 1),
                    minute = minute + minutes
                )
            )
        }

        if (isEmpty()) {
            add(
                copy(
                    resources = resources.mapValues { it.value + robots.getOrDefault(it.key, 0) * remainingTime },
                    minute = minute + remainingTime
                )
            )
        }
    }

    companion object {
        val INITIAL_ROBOTS = mapOf("ore" to 1)
        val EMPTY_RESOURCE = mapOf("ore" to 0, "clay" to 0, "geode" to 0, "obsidian" to 0)
    }
}


fun explore(start: State): State? {
    var lowerBound = 0
    var bestKnown: State? = null
    val ordering = start.blueprint.ratio()
    val scoring = compareByDescending<State> { it.robot("geode") * it.remainingTime }
        .thenByDescending { it.robot(ordering[0]) }
        .thenByDescending { it.robot(ordering[1]) }
        .thenByDescending { it.robot("ore") }

    val frontier = PriorityQueue<State>(scoring).apply { add(start) }

    var i = 0
    while (frontier.isNotEmpty()) {
        i++
        val next = frontier.remove()

        if (next.done && next.resource("geode") > lowerBound) {
            bestKnown = next
            //println(bestKnown)
            lowerBound = bestKnown.resource("geode")
        } else if (next.minute < next.maxTime) {
            frontier.addAll(next.options())
        }
    }

    return bestKnown
}

blueprints
    .mapValues { explore(State(blueprints[it.key]!!, State.EMPTY_RESOURCE, State.INITIAL_ROBOTS, 0, 24)) }
    .entries
    .fold(0) { acc, (blueprintId, state) -> acc + blueprintId * (state?.resource("geode") ?: 0)}
    .let(::println)

blueprints
    .toSortedMap()
    .headMap(3 + 1)
    .mapValues { explore(State(blueprints[it.key]!!, State.EMPTY_RESOURCE, State.INITIAL_ROBOTS, 0, 32)) }
    .entries
    .fold(1) { acc, (_, state) -> acc * (state?.resource("geode") ?: 0)}
    .let(::println)

