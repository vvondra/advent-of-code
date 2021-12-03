import java.io.File

val nums = File("03.input")
    .readLines()
    .map { it.map { it.digitToInt() } }

val freqs = nums.fold(emptyMap<Int, Pair<Int, Int>>()) { counts, reading ->
    (counts.keys + (0 until reading.count()))
        .associateWith {
            Pair(
                (counts[it]?.first ?: 0) + (1 - reading[it]),
                (counts[it]?.second ?: 0) + reading[it]
            )
        }
}

val mostCommon = freqs.keys.sorted().map { if (freqs[it]!!.first > freqs[it]!!.second) 0 else 1 }
val gamma = mostCommon.joinToString("").toInt(2)
val epsilon = mostCommon.map { 1 - it }.joinToString("").toInt(2)

println(gamma * epsilon)

fun determineRating(max: Boolean): Int {
    fun filter(iteration: List<List<Int>>, index: Int): List<List<Int>> =
        if (iteration.size <= 1) {
            iteration
        } else {


            val bit = if (max) mostCommon[index] else 1 - mostCommon[index]
            println(iteration)
            println(index)
            println(bit)
            println("===")
            filter(iteration.filter { it[index] == bit }, index + 1)
        }

    return filter(nums, 0).first().joinToString("").toInt(2)
}

println(determineRating(max = true))
println(determineRating(max = false))

println(determineRating(max = true) * determineRating(max = false))
