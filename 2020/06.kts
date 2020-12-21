import java.io.File

val groups = File("6.input").readText()
  .split("\n\n")
  .map { it.split("\n").map { it.split("").filter(String::isNotEmpty).toSet() } }

groups
  .map { it.reduce { acc, set -> acc.union(set) }.count() }
  .sum()
  .let(::println)

groups
  .map { it.reduce { acc, set -> acc.intersect(set) }.count() }
  .sum()
  .let(::println)
