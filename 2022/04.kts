import java.io.File

val ranges = File("input/04.in").readLines()
    .map {
        it.split(",")
            .map {
                it.split("-").let { IntRange(it.first().toInt(), it.last().toInt()) }
            }
    }

fun IntRange.intersect(b: IntRange): IntRange = IntRange(Math.max(start, b.start), Math.min(endInclusive, b.endInclusive))
fun IntRange.length(): Int = when (this.isEmpty()) {
    true -> 0
    else -> endInclusive - start + 1
}

ranges.count { r -> r.reduce { a, b -> a.intersect(b) }.length() == r.minOf { it.length() } }
    .let(::println)
ranges.count { r -> r.reduce { a, b -> a.intersect(b) }.length() > 0 }
    .let(::println)
