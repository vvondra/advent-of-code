import java.io.File

val input = File("03.input")
    .readLines()
    .map { it.map { it.digitToInt() } }

fun mostCommon(nums: List<List<Int>>): List<Int> {
    fun frequencies(): Map<Int, Pair<Int, Int>> =
        nums.fold(emptyMap<Int, Pair<Int, Int>>()) { counts, reading ->
            (counts.keys + (0 until reading.count()))
                .associateWith {
                    Pair(
                        (counts[it]?.first ?: 0) + (1 - reading[it]),
                        (counts[it]?.second ?: 0) + reading[it]
                    )
                }
        }

    val freqs = frequencies()
    return freqs.keys.sorted().map { if (freqs[it]!!.first > freqs[it]!!.second) 0 else 1 }
}

val inputMostCommon = mostCommon(input)
val gamma = inputMostCommon.joinToString("").toInt(2)
val epsilon = inputMostCommon.map { 1 - it }.joinToString("").toInt(2)

println(gamma * epsilon)

fun determineRating(max: Boolean): Int {
    tailrec fun filter(iteration: List<List<Int>>, index: Int): List<List<Int>> =
        if (iteration.size <= 1) {
            iteration
        } else {
            val iterationMostCommon = mostCommon(iteration)
            val bit = if (max) iterationMostCommon[index] else 1 - iterationMostCommon[index]
            filter(iteration.filter { it[index] == bit }, index + 1)
        }

    return filter(input, 0).first().joinToString("").toInt(2)
}

println(determineRating(max = true) * determineRating(max = false))
