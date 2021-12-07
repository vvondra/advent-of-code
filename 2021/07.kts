import java.io.File
import java.lang.Math.abs

val input = File("07.input")
    .readText()
    .split(",").map(String::toInt)

val range = input.minOrNull()!!..input.maxOrNull()!!

range.minOfOrNull { pos -> input.map { abs(pos - it) }.sum() }?.let(::println)

range.minOfOrNull { pos ->
    input
        .map { abs(pos - it) }
        .map { (it * (it + 1)) / 2 }
        .sum()
}?.let(::println)