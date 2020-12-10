import java.io.File
import java.util.*
import kotlin.math.max
import kotlin.system.measureTimeMillis

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
class Chain(val jolt: Int, val next: Chain? = null) {
  var lookup: Map<Int, Chain> = emptyMap();
  init {
    var ret = this
    for (i in 0 until maxJump + 1) {
      lookup = lookup.plus(i to ret)
      if (ret.next == null) break
      ret = ret.next!!
    }
  }

  fun getOrNull(n: Int): Int? = lookup.get(n)?.jolt
  fun drop(n: Int): Chain? = lookup.get(n)

  companion object {
    fun fromList(list: List<Int>): Chain =
      list.reversed().drop(1).fold(Chain(list.last())) { chain, i -> Chain(i, chain) }
  }
}

var memo = mutableMapOf<Pair<Int, Chain>, Long>()
fun count(start: Int, tail: Chain?): Long {
  if (tail == null) return 1
  if (memo.containsKey(start to tail)) return memo[start to tail]!!

  return (1..maxJump)
    .map { skip ->
      val head = tail.getOrNull(skip - 1)
      if (head == null) 0
      else if (head - start > maxJump) 0
      else count(head, tail.drop(skip))
    }
    .fold(0, Long::plus)
    .also { memo[start to tail] = it }
}

count(chain.first(), Chain.fromList(chain.drop(1))).let(::println)
