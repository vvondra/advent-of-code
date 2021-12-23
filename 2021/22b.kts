import java.io.File

data class Range(val start: Int, val endInclusive: Int) {
    fun intersects(o: Range): Boolean = start <= o.endInclusive && o.start <= endInclusive
    fun intersect(o: Range): Range = Range(maxOf(start, o.start), minOf(endInclusive, o.endInclusive))
}
data class Ins(val on: Boolean, val x: Range, val y: Range, val z: Range) {
    fun intersects(o: Ins) = x.intersects(o.x) && y.intersects(o.y) && z.intersects(o.z)
    fun count() = (x.endInclusive - x.start + 1) *
            (y.endInclusive - y.start + 1) *
            (z.endInclusive - z.start + 1)
}
data class XYZ(val x: Int, val y: Int, val z: Int)

val ins = File("22.input").readLines()
    .map { line ->
        val lineParts = line.split(" ")
        val on = lineParts[0] == "on"

        lineParts[1].split(",")
            .map { coords ->
                coords.drop(2).split("..").let { Range(it[0].toInt(), it[1].toInt()) }
            }.let { Ins(on, it[0], it[1], it[2]) }
    }


val total = ins.fold(0L to emptySet<Ins>()) { (count, counted), next ->
    if (counted.none { it.intersects(next) }) {
        println("new!==")
        count + next.count() to counted + next
    } else {
        println("old!")
        count to counted
    }
}

val minZ = ins.minOf { it.z.start }
val maxZ = ins.maxOf { it.z.endInclusive }

println(minZ to maxZ)

println(total)