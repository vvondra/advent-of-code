import java.io.File

val signal = File("input/06.in").readText()

fun firstDistinct(size: Int) = signal
    .windowed(size)
    .withIndex()
    .find { (_, four) -> four.toSet().size == size }!!
    .let { it.index + size }

println(firstDistinct(4))
println(firstDistinct(14))
