import java.io.File

fun priority(c: Char) = when {
    c.isLowerCase() -> c - 'a' + 1
    c.isUpperCase() -> c - 'A' + 27
    else -> throw Exception()
}

fun score(rucksacks: List<String>, chunker: (List<String>) -> List<List<String>>) = chunker(rucksacks)
        .map { it.map(String::toSet).reduce(Set<Char>::intersect).single() }
        .sumOf(::priority)

val input = File("input/03.in").readLines()

score(input) { r -> r.map { listOf(it.substring(0, it.length / 2), it.substring(it.length / 2)) } }
    .let(::println)

score(input) { it.chunked(3) }
    .let(::println)
