import java.io.File
import java.math.BigInteger

val input = File("06.input")
    .readText()
    .split(",").map(String::toInt)
    .groupingBy { it }.eachCount()
    .mapValues { BigInteger.valueOf(it.value.toLong()) }

val CYCLE = 6
val NEW_FISH_CYCLE = 2

fun Iterable<BigInteger>.sum(): BigInteger = this.reduce { acc, integer -> acc + integer }

fun fishAtDay(day: Int): BigInteger =
    generateSequence(input) { freqs ->
        freqs
            .mapKeys { entry -> entry.key - 1 }
            .toMutableMap()
            .apply {
                if (this.containsKey(-1)) {
                    this.merge(CYCLE, this[-1]!!) { a, b -> a + b }
                    this.merge(CYCLE + NEW_FISH_CYCLE, this[-1]!!) { a, b -> a + b }
                    this.remove(-1)
                }
            }
    }.elementAt(day).values.sum()

println(fishAtDay(80))
println(fishAtDay(256))
