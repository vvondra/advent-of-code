import java.io.File

typealias Signal = Set<Char>

val input = File("08.input")
    .readLines()
    .map {
        it.split(" | ").let {
            it[0].split(" ").map(String::toSet) to it[1].split(" ").map(String::toSet)
        }
    }

fun candidates(word: Set<Char>): Set<Int> = when (word.size) {
    2 -> setOf(1)
    3 -> setOf(7)
    4 -> setOf(4)
    5 -> setOf(2, 3, 5)
    6 -> setOf(6, 9, 0)
    7 -> setOf(8)
    else -> throw Exception("Unknown")
}

fun code(patterns: List<Signal>, output: List<Signal>): List<Int> {
    val patternMap = patterns.associate { it to candidates(it) }

    fun findNum(num: Int) = patternMap.filterValues { it == setOf(num) }.keys.single()

    val one = findNum(1)
    val four = findNum(4)
    val decodedPatternsMap = patternMap.mapValues { (signal, candidates) ->
        when(candidates) {
            setOf(6, 9, 0) -> when {
                signal.intersect(one).size == 1 -> 6
                signal.intersect(four).size == 4 -> 9
                else -> 0
            }
            setOf(2, 3, 5) -> when {
                signal.intersect(one).size == 2 -> 3
                signal.intersect(four).size == 3 -> 5
                else -> 2
            }
            else -> candidates.single()
        }
    }

    return output.map { decodedPatternsMap[it]!! }
}

val codes = input.map { code(it.first, it.second) }

println(codes.map { output -> output.count { setOf(1, 4, 7, 8).contains(it) } }.sum())
println(codes.map { it.joinToString("").toInt() }.sum())