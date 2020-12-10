import java.io.File

val adapters = File("10.input").readLines().map(String::toInt).sorted()

val chain = listOf(0) + adapters + listOf(adapters.maxOrNull()!!.plus(3))
chain.windowed(2)
  .map { (a, b) -> b - a }
  .groupingBy { it }
  .eachCount()
  .let { println(it[1]!! * it[3]!!) }
