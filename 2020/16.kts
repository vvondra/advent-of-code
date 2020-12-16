import java.io.File

val parts = File("16.input").readText().split("\n\n")

data class Constraint(val name: String, val rules: List<IntRange>) {
  fun matches(n: Int) = rules.any { n in it }
}

val constraints = parts[0].split("\n")
  .map {
    it.split(": ")
      .let {
        Constraint(
          it[0],
          it[1].split(" or ").map { it.split("-").let { IntRange(it[0].toInt(), it[1].toInt()) } }
        )
      }
  }

val ticket = parts[1].split("\n").last().split(",").map(String::toInt)
val nearby = parts[2].split("\n")
  .drop(1)
  .filter { it.isNotEmpty() }
  .map { it.split(",").map(String::toInt) }

val totallyInvalid = nearby
  .flatMap { it.filter { value -> constraints.none { it.matches(value) } } }
  .sum()
  .let(::println)

fun findMappings(mappings: List<List<Int>>): List<Int> {
  fun eliminateMappings(eliminated: List<List<Int>>): List<List<Int>> {
    val taken = eliminated.filter { it.size == 1 }.flatten()

    if (taken.size == eliminated.size) return eliminated
    else return eliminateMappings(
      eliminated.map {
        if (it.size == 1) it
        else it.filterNot { it in taken }
      }
    )
  }

  val valid = nearby.filter { it.all { value -> constraints.any { it.matches(value) } } }

  val feasible = mappings.withIndex()
    .map { (fieldIdx, constraintCandidates) ->
      constraintCandidates.filter { constraintIdx ->
        valid
          .map { it[fieldIdx] }
          .all { constraints[constraintIdx].matches(it) }
      }
    }

  return eliminateMappings(feasible).map { it.single() }
}

val mapping = findMappings((0 until ticket.size).map { (0 until constraints.size).toList() })

val errorRate = ticket.withIndex()
  .map { (field, value) ->
    if (constraints[mapping[field]].name.startsWith("departure")) value.toLong()
    else null
  }
  .filterNotNull()
  .reduce(Long::times)
  .let(::println)
