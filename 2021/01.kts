import java.io.File

val depths = File("01.input").readLines().map(String::toInt)

depths.windowed(2)
    .count { it[0] < it[1] }
    .let(::println)

depths.windowed(3)
    .windowed(2)
    .count { it[0].sum() < it[1].sum() }
    .let(::println)
