import java.io.File

val cubes = File("input/18.in")
    .readLines()
    .map { it.split(",").let { (x, y, z) -> XYZ(x.toInt(), y.toInt(), z.toInt()) } }
    .toSet()

data class XYZ(val x: Int, val y: Int, val z: Int) {
    fun adjacent(): Set<XYZ> = listOf(
        XYZ(0, 0, -1),
        XYZ(0, 0, 1),
        XYZ(1, 0, 0),
        XYZ(-1, 0, 0),
        XYZ(0, 1, 0),
        XYZ(0, -1, 0)
    ).map { it + this }.toSet()

    operator fun plus(other: XYZ) = XYZ(x + other.x, y + other.y, z + other.z)
}

val surface = cubes.sumOf { cube -> cube.adjacent().count { it !in cubes } }
println(surface)

val seeds = cubes.flatMap { it.adjacent() }.filterNot { it in cubes }.toSet()
fun pocket(seed: XYZ): Set<XYZ> {
    val frontier = mutableListOf(seed)
    val visited = mutableSetOf<XYZ>()
    while (frontier.isNotEmpty()) {
        if (frontier.size > 1000) return emptySet()
        val next = frontier.removeFirst()
        if (next !in visited) {
            visited.add(next)
            frontier.addAll(next.adjacent().filterNot { it in cubes })
        }
    }

    return visited
}

val pockets = seeds.fold(mutableSetOf<XYZ>()) { acc, seed -> acc.apply { addAll(pocket(seed)) } }

val extSurface = cubes.sumOf { cube -> cube.adjacent().count { it !in pockets && it !in cubes } }
println(extSurface)
