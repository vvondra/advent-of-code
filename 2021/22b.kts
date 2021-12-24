import java.io.File

data class Ins(val on: Boolean, val x: IntRange, val y: IntRange, val z: IntRange) {
    fun intersects(o: Ins) = x.intersect(o.x).isNotEmpty() && y.intersect(o.y).isNotEmpty() && z.intersect(o.z).isNotEmpty()
    fun count() = (x.endInclusive - x.start + 1) *
            (y.endInclusive - y.start + 1) *
            (z.endInclusive - z.start + 1)
}

val ins = File("22.input").readLines()
    .map { line ->
        val lineParts = line.split(" ")
        val on = lineParts[0] == "on"

        lineParts[1].split(",")
            .map { coords ->
                coords.drop(2).split("..").let { IntRange(it[0].toInt(), it[1].toInt()) }
            }.let { Ins(on, it[0], it[1], it[2]) }
    }

fun mapCells(axis: (Ins) -> IntRange) =
    ins
        .flatMap { axis(it).let { a -> listOf(a.start, a.endInclusive + 1) } }
        .sorted()
        .distinct()
        .withIndex()

val xIndexMap = mapCells { it.x }.associate { (index, i) -> index to i }
val xMap = mapCells { it.x }.associate { it.value to it.index }
val yIndexMap = mapCells { it.y }.associate { (index, i) -> index to i }
val yMap = mapCells { it.y }.associate { it.value to it.index }
val zIndexMap = mapCells { it.z }.associate { (index, i) -> index to i }
val zMap = mapCells { it.z }.associate { it.value to it.index }

val mappedIns = ins.map {
    Ins(
        it.on,
        xMap[it.x.start]!!..(xMap[it.x.endInclusive + 1]!! - 1),
        yMap[it.y.start]!!..(yMap[it.y.endInclusive + 1]!! - 1),
        zMap[it.z.start]!!..(zMap[it.z.endInclusive + 1]!! - 1),
    )
}

val grid = Array(xIndexMap.size) { Array(yIndexMap.size) { Array(zIndexMap.size) { false } } }

mappedIns.forEach { next ->
    for (i in next.x) {
        for (j in next.y) {
            for (k in next.z) {
                if (next.on) {
                    grid[i][j][k] = true
                } else {
                    grid[i][j][k] = false
                }
            }
        }
    }
}

var volume = 0L
for (i in 0 until xIndexMap.size) {
    for (j in 0 until yIndexMap.size) {
        for (k in 0 until zIndexMap.size) {
            if (grid[i][j][k]) {
                volume += (xIndexMap[i + 1]!! - xIndexMap[i]!!).toLong() *
                        (yIndexMap[j + 1]!! - yIndexMap[j]!!).toLong() *
                        (zIndexMap[k + 1]!! - zIndexMap[k]!!).toLong()
            }
        }
    }
}

println(volume)