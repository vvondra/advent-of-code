import java.io.File

data class XY(val x: Int, val y: Int)
data class Fold(val up: Boolean, val n: Int)

val inputs = File("13.input")
    .readText()
    .split("\n\n")
    .map(String::trim)

val xys = inputs[0].lines().map { it.split(",").let { XY(it[0].toInt(), it[1].toInt()) } }.toSet()
val folds = inputs[1].lines().map {
    it.split(" ").let { f ->
        f[2].split("=").let { Fold(it[0] == "y", it[1].toInt()) }
    }
}

fun fold(map: Set<XY>, fold: Fold): Set<XY> {
    val (new, old) = map
        .partition { if (fold.up) it.y < fold.n else it.x < fold.n }
        .let { it.first.toSet() to it.second.toSet() }

    return old.fold(new) { acc, xy ->
        acc + if (fold.up) XY(xy.x, fold.n - (xy.y - fold.n)) else XY(fold.n - (xy.x - fold.n), xy.y)
    }
}

println(fold(xys, folds.first()).size)

folds.fold(xys, ::fold).let { map ->
    for (y in 0..map.maxOf { it.y }) {
        for (x in 0..map.maxOf { it.x }) {
            print(if (map.contains(XY(x, y))) '#' else ' ')
        }
        println()
    }
}