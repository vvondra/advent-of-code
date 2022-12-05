import java.io.File

val ranges = File("input/04.in").readLines()
    .map {
        it.split(",")
            .map {
                it.split("-").map { it.toInt() }.let { IntRange(it.first(), it.last()) }
            }
    }

fun IntRange.intersect(b: IntRange): IntRange = IntRange(Math.max(start, b.start), Math.min(endInclusive, b.endInclusive))
fun IntRange.length(): Int = if (this.isEmpty()) 0 else endInclusive - start + 1

ranges.count { r -> r.reduce { a, b -> a.intersect(b) }.length() == r.minOf { it.length() } }
    .let(::println)
ranges.count { r -> r.reduce { a, b -> a.intersect(b) }.length() > 0 }
    .let(::println)
