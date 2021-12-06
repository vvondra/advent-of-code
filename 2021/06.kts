import java.io.File

val input = File("06.input")
    .readText()
    .split(",").map(String::toInt)
    .groupingBy { it }.eachCount()

val CYCLE = 6
val NEW_FISH_CYCLE = 2

val last = generateSequence(input) { freqs ->
    freqs.keys.minOrNull()?.let { nextHatch ->
        freqs
            .toMutableMap()
            .apply {
                this.put(CYCLE, this.get(0)!!)
                this.put(CYCLE + NEW_FISH_CYCLE, this.get(0)!!)
                this.remove(0)
            }
            .mapKeys { entry -> entry.key - nextHatch }
            .toMutableMap()

            .toMap()
            .also {
                println(it)
            }
    }
}

println(last.elementAt(19).values.sum())