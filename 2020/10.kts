import java.io.File

val adapters = File("10.input").readLines().map(String::toInt).sorted()

val chain = listOf(0) + adapters + listOf(adapters.maxOrNull()!!.plus(3))
chain.windowed(2)
  .map { (a, b) -> b - a }
  .groupingBy { it }
  .eachCount()
  .let { println(it[1]!! * it[3]!!) }

fun count(start: Int, tail: List<Int>): Long {
  if (tail.isEmpty()) return 1
  return (1..(minOf(3, tail.count()))).map { skip ->
    val head = tail[skip - 1]
    if (head - start > 3) 0
    else count(head, tail.drop(skip))
  }.fold(0, Long::plus)
}

count(chain.first(), chain.drop(1)).let(::println)
