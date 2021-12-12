import java.io.File


val edges = File("12.input")
    .readLines()
    .map { it.split("-").let { it[0] to it[1] } }
    .fold(mutableMapOf<String, List<String>>()) { acc, pair ->
        acc.merge(pair.first, listOf(pair.second), List<String>::plus)
        acc.merge(pair.second, listOf(pair.first), List<String>::plus)
        acc
    }


fun explore(): Int {
    fun step(cave: String, visited: Set<String>): Int = when (cave) {
            "end" -> 1
            else -> edges[cave]!!
                        .filterNot { visited.contains(it) }
                        .fold(0) { acc, s ->
                            acc + step(s, if (cave.lowercase() == cave) visited.plus(cave) else visited)
                        }
        }

    return step("start", emptySet<String>())
}

fun explore2(): Int {
    fun step(cave: String, visited: Set<String>, path: List<String>, small: String?): List<List<String>> = when (cave) {
        "end" -> listOf(path + listOf("end"))
        else -> edges[cave]!!
            .filterNot { visited.contains(it) }
            .fold(emptyList()) { acc, s ->
                acc + if (small == null && cave.lowercase() == cave) {
                    step(s, visited, path.plus(s), cave) + step(s, visited.plus(cave), path.plus(s),null)
                } else {
                    step(s, if (cave.lowercase() == cave) visited.plus(cave) else visited, path.plus(s), small)
                }
            }
    }

    val paths = step("start", setOf<String>("start"), emptyList(),null)

    return paths.distinct().count()
}

println(explore())
println(explore2())