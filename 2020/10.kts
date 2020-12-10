import java.io.File
import java.util.*

val adapters = File("10.input").readLines().map(String::toInt).sorted()

data class Chain(val jolt: Int, val next: Chain? = null) {
  fun getOrNull(n: Int): Int? {
    var ret = this
    for (i in 0 until n) {
      if (ret.next == null) return null
      ret = ret.next!!
    }
    return ret.jolt
  }

  fun drop(n: Int): Chain? {
    var ret = this
    for (i in 0 until n) {
      if (ret.next == null) return null
      ret = ret.next!!
    }
    return ret
  }

  companion object {
    fun fromList(list: List<Int>): Chain {
      return list.reversed().fold(Chain(list.last())) { chain, i -> Chain(i, chain) }
    }
  }
}

val chain = listOf(0) + adapters + listOf(adapters.maxOrNull()!!.plus(3))
chain.windowed(2)
  .map { (a, b) -> b - a }
  .groupingBy { it }
  .eachCount()
  .let { println(it[1]!! * it[3]!!) }

fun count(start: Int, tail: Chain?): Long {
  if (tail == null) return 1

  return (1..3).map { skip ->
    val head = tail.getOrNull(skip - 1)
    if (head == null) 0
    else if (head - start > 3) 0
    else count(head, tail.drop(skip))
  }.fold(0, Long::plus)
}

count(chain.first(), Chain.fromList(chain.drop(1))).let(::println)
