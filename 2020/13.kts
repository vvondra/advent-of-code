import java.io.File

val timetables = File("13.input").readLines()
  .let { it[0].toLong() to it[1].split(",").filter { it != "x" }.map(String::toLong) }

timetables.second.map { it to timetables.first % it }
  .minByOrNull { (it.first - it.second) }
  ?.let { println(it.first * (it.first - it.second)) }

val constraints = File("13.input").readLines()
  .last().split(",").map(String::toIntOrNull)
  .withIndex().filter { it.value != null }
  .map { it.value!! to Math.floorMod(it.value!! - it.index, it.value!!) }
  .map { (a, b) -> a.toLong() to b.toLong() }
  .sortedByDescending { it.first }

fun getSequence(mod: Long, remainder: Long): Sequence<Long> =
  generateSequence(1L to remainder) { (i, _) -> i + 1 to remainder + i * mod }.map { it.second }

constraints
  .reduce { (n, a), (m, b) ->
    getSequence(n, a)
      .find { it % m == b }!!
      .let { (m * n) to it }
  }
  .let { println(it.second) }

