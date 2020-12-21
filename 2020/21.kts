import java.io.File

val list = File("21.input").readLines()
  .map { it.split(" (contains ").let { it[0].split(" ") to it[1].dropLast(1).split(", ") } }

val ingredients = list.flatMap { it.first }.toSet()
val allergens = list.flatMap { it.second }.toSet()

fun match(): Map<String, String> {
  var start = ingredients
  val mapping = mutableMapOf<String, String>()
  while (true) {
    start = allergens.fold(start) { ing, allergen ->
      val res = list.filter { it.second.contains(allergen) }
        .map { it.first.filter { it in ing }.toSet() }
        .reduce { i, j -> i.intersect(j) }

      if (res.size == 1) {
        mapping.put(allergen, res.single())
        ing.filterNot { it == res.single() }.toSet()
      } else ing
    }

    if (mapping.size == allergens.size) {
      break
    }
  }

  return mapping
}

val mapping = match()
val safe = ingredients subtract mapping.values

list.map { (it.first.toSet() intersect safe).size }.sum().let(::println)
mapping.entries.sortedBy { it.key }.map { it.value }.joinToString(",").let(::println)
