import java.io.File

val groups = File("6.input").readText()
    .split("\n\n")
    .map { it.split("\n").map { it.split("").filter(String::isNotEmpty).toSet() } }

val union = groups
    .map { it.reduce { acc, set -> acc.union(set) }.count() }
    .sum()

println(union)

val intersection = groups
    .map { it.reduce { acc, set -> acc.intersect(set) }.count() }
    .sum()

println(intersection)