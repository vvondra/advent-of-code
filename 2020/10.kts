import java.io.File

val adapters = File("10.input").readLines().map(String::toInt).sorted()
val maxJump = 3

// Part 1
val chain = listOf(0) + adapters + listOf(adapters.maxOrNull()!!.plus(maxJump))
chain.windowed(2)
  .map { (a, b) -> b - a }
  .groupingBy { it }
  .eachCount()
  .let { println(it[1]!! * it[3]!!) }


// Part 2
var memo = mutableMapOf<Pair<Int, Int>, Long>()
fun count(start: Int, used: Int): Long {
  if (used == chain.count()) return 1
  if (memo.containsKey(start to used)) return memo[start to used]!!

  return (1..maxJump)
    .map { skip ->
      val head = chain.getOrNull(used + skip - 1)
      if (head == null) 0
      else if (head - start > maxJump) 0
      else count(head, used + skip)
    }
    .sum()
    .also { memo[start to used] = it }
}

count(chain.first(), 1).let(::println)
