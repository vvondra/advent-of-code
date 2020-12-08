import java.io.File;

// {shiny gold={bright lavender=4, striped maroon=1, plaid silver=2}, bright lavender={...}
val bags = File("7.input").readLines()
  .map { it.split(" bags contain ") }
  .associate { it[0] to it[1] }
  .mapValues {
    it.value
      .split(",")
      .map { it.trim().replace(Regex(" bags?\\.?$"), "") }
      .map { it.split(" ", limit = 2) }
      .associate { it[1] to it[0] }
      .mapValues { it.value.replace("no", "0") }
      .mapValues { it.value.toInt() }
  }

fun containsBag(desired: String, current: String): Boolean {
  val bag = bags.get(current).orEmpty()

  if (bag.getOrDefault(desired, 0) > 0) return true

  return bag.keys.any { containsBag(desired, it) }
}

fun countBags(color: String): Int {
  return bags.getOrDefault(color, emptyMap())
    .map { it.value + it.value * countBags(it.key) }
    .sum()
}

bags.keys.map { containsBag("shiny gold", it) }.count { it }.let(::println)
countBags("shiny gold").let(::println)
