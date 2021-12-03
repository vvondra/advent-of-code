import java.io.File

val input = File("03.input")
    .readLines()
    .map { it.map { it.digitToInt() } }

fun List<Int>.fromBitList(): Int = this.joinToString("").toInt(radix = 2)
fun Int.flipBit(): Int = 1 - this

fun mostCommon(nums: List<List<Int>>): List<Int> {
    fun frequencies(): Map<Int, Pair<Int, Int>> =
        nums.fold(emptyMap()) { counts, reading ->
            (counts.keys + (0 until reading.count()))
                .associateWith {
                    Pair(
                        (counts[it]?.first ?: 0) + reading[it].flipBit(),
                        (counts[it]?.second ?: 0) + reading[it]
                    )
                }
        }

    return frequencies().let { freqs ->
        freqs.keys.sorted().map { if (freqs[it]!!.first > freqs[it]!!.second) 0 else 1 }
    }
}

val inputMostCommon = mostCommon(input)
val gamma = inputMostCommon.fromBitList()
val epsilon = inputMostCommon.map { it.flipBit() }.fromBitList()

println(gamma * epsilon)

fun determineRating(max: Boolean): Int {
    tailrec fun filter(iteration: List<List<Int>>, index: Int): List<List<Int>> =
        if (iteration.size <= 1) {
            iteration
        } else {
            val iterationMostCommon = mostCommon(iteration)
            val bit = if (max) iterationMostCommon[index] else iterationMostCommon[index].flipBit()
            filter(iteration.filter { it[index] == bit }, index + 1)
        }

    return filter(input, 0).first().fromBitList()
}

println(determineRating(max = true) * determineRating(max = false))
