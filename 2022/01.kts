import java.io.File

val elves = File("input/01.in")
    .readText()
    .trim()
    .split("\n\n")
    .map { it.split("\n").map { it.toLong() } }


println(elves.maxOf(List<Long>::sum))
println(elves.map { it.sum() }.sortedDescending().take(3).sum())
