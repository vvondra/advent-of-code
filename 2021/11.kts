import java.io.File
import java.util.*

data class XY(val x: Int, val y: Int) {
    val dirs = listOf(1 to 0, -1 to 0, 0 to 1, 0 to -1, -1 to 1, -1 to -1, 1 to 1, 1 to -1)
    fun adjacent(): Set<XY> = dirs.map { XY(this.x + it.first, this.y + it.second) }.toSet()
}

val input: Map<XY, Int> = File("11.input")
    .readLines()
    .withIndex().flatMap { (line, row) ->
        row.withIndex().map { (col, char) ->
            XY(line, col) to char.digitToInt()
        }
    }
    .toMap()

val simulation = generateSequence(input to 0) { (last, flashes) ->
    last.mapValues { it.value + 1 }
        .toMutableMap()
        .run {
            var alreadyFlashed = mutableSetOf<XY>()
            do {
                var flashed = false
                val candidates = filterValues { it > 9 }
                    .keys
                    .filterNot(alreadyFlashed::contains)

                candidates
                    .map {
                        flashed = true
                        alreadyFlashed.add(it)
                        it
                    }
                    .flatMap { it.adjacent() }
                    .filter { containsKey(it) }
                    .forEach { set(it, getValue(it) + 1) }

            } while (flashed)
            alreadyFlashed.forEach { set(it, 0) }

            this to alreadyFlashed.size
        }
        .let {
            if (it.second == it.first.size)
                null
            else
                it.first.toMap() to flashes + it.second
        }
}


simulation.elementAt(100).let { println(it.second) }
println(simulation.count())
