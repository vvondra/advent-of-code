import java.io.File
import java.util.function.BiFunction

data class XYZ(val x: Int, val y: Int, val z: Int) {
    fun manhattan(other: XYZ) = Math.abs(x - other.x) + Math.abs(y - other.y) + Math.abs(z - other.z)
    operator fun minus(other: XYZ) = XYZ(x - other.x, y - other.y, z - other.z)
    operator fun plus(other: XYZ) = XYZ(x + other.x, y + other.y, z + other.z)
    override fun toString(): String = "${x},${y},${z}"
    fun rotate(): XYZ = XYZ(x, z, -y)
}

val transforms: List<(XYZ) -> XYZ> = listOf<(XYZ) -> XYZ>(
        { a -> a },
        { a -> XYZ(a.y, -a.x, a.z) },
        { a -> XYZ(-a.x, -a.y, a.z) },
        { a -> XYZ(-a.y, a.x, a.z) },
        { a -> XYZ(a.z, a.y, -a.x) },
        { a -> XYZ(-a.z, a.y, a.x) },
    ).flatMap { t ->
        (1..4).map<Int, (XYZ) -> XYZ> { rotations ->
            { xyz ->
                var r = t(xyz)
                for (i in 1..rotations) r = r.rotate()
                r
            }
        }
    }

val obs = File("19.input").readText().split("\n\n")
    .map(String::trim).map(String::lines)
    .map { it.drop(1).map { it.split(",").let { XYZ(it[0].toInt(), it[1].toInt(), it[2].toInt() ) } } }

fun findMapping(first: List<XYZ>, second: List<XYZ>): ((XYZ) -> XYZ)? {
    first.forEach { base ->
        second.forEach { other ->
            // I assume other = base in all transformations
            transforms.forEach { mapping ->
                val remappedOther = mapping(other)
                val translation = base - remappedOther

                second.count { testOther -> first.contains(mapping(testOther) + translation) }
                    .let {
                        if (it >= 12) {
                            return fun(xyz: XYZ): XYZ { return mapping(xyz) + translation }
                        }
                    }
            }
        }
    }

    return null
}

fun explore(): Map<Int, (XYZ) -> XYZ> {
    var remaining = (1 until obs.size).toSet()
    var mappings: Map<Int, (XYZ) -> XYZ> = mapOf(0 to { it })
    while (remaining.isNotEmpty()) {
        remaining.forEach rem@ { rem ->
            mappings.forEach { (start, mapper) ->
                val mapping = findMapping(obs[start], obs[rem])
                if (mapping != null) {
                    mappings += rem to fun(xyz: XYZ): XYZ { return mapper(mapping(xyz)) }
                    remaining -= rem
                    return@rem
                }
            }
        }
    }

    return mappings
}

val final = explore()

val intersect = obs.withIndex().map { (i, o) -> o.map(final.getValue(i)).toSet() }
    .reduce { a, b -> a.union(b) }

println(intersect.size)