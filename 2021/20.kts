import java.io.File

data class XY(val x: Int, val y: Int) {
    val dirs = listOf(-1 to -1, -1 to 0, -1 to 1, 0 to -1, 0 to 0, 0 to 1, 1 to -1, 1 to 0, 1 to 1)
    fun adjacent(): List<XY> = dirs.map { XY(x + it.first, y + it.second) }
}
val (algo, input) = File("20.input").readText().split("\n\n")
    .let {
        it[0].trim() to it[1].trim().lines().withIndex().flatMap { (line, row) ->
            row.withIndex().map { (col, char) -> XY(line, col) to char }
        }.toMap().withDefault { '.' }
    }

fun render(map: Map<XY, Char>) {
    for (x in map.keys.minOf { it.x }..map.keys.maxOf { it.x }) {
        for (y in map.keys.minOf { it.y }..map.keys.maxOf { it.y }) {
            print(map.getValue(XY(x, y)))
        }
        println()
    }
}

// super lazy, horribly inefficient
fun pad(map: Map<XY, Char>, default: Char): Map<XY, Char> {
    var final = map
    repeat(3) {
        final = final
            .flatMap { orig -> orig.key.adjacent().map { it to map.getOrDefault(it, default) } + listOf(orig.key to orig.value) }
            .toMap()
    }

    return final
}

val seq = generateSequence(input to 0) { (image, i) ->
    pad(image, if (i % 2 == 0) '.' else '#')
        .mapValues { (k, _) ->
            algo.get(
                k.adjacent().map { if (image.getValue(it) == '#') 1 else 0 }.joinToString("").toInt(2)
            )
        }
        .withDefault { if (i % 2 == 0) '#' else '.' } to i + 1
}.map { it.first }

val lit = seq.elementAt(2).count { (_, v) -> v == '#' }
println(lit)

val lit2 = seq.elementAt(50).count { (_, v) -> v == '#' }
println(lit2)