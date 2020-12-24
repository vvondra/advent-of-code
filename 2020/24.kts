import java.io.File

data class Hex(val q: Int, val r: Int) {
  fun move(dir: String): Hex = this + axialDirections.get(dir)!!
  fun neighbours(): Set<Hex> = axialDirections.values.map { this + it }.toSet()
  operator fun plus(other: Hex): Hex = Hex(q + other.q, r + other.r)

  companion object {
    val axialDirections = mapOf(
      "e" to Hex(+1, 0),
      "ne" to Hex(+1, -1),
      "nw" to Hex(0, -1),
      "w" to Hex(-1, 0),
      "sw" to Hex(-1, +1),
      "se" to Hex(0, +1),
    )
  }
}

enum class Color { White, Black }

fun parse(seq: String): Sequence<String> {
  val tokens = listOf("e", "se", "sw", "w", "nw", "ne").sortedByDescending { it.length }
  return sequence {
    var suffix = seq
    while (suffix.isNotEmpty()) {
      val token = tokens.find { suffix.startsWith(it) }!!
      yield(token)
      suffix = suffix.drop(token.length)
    }
  }
}

val floor = File("24.input").readLines()
  .map(::parse)
  .map { it.fold(Hex(0, 0), Hex::move) }
  .groupingBy { it }
  .eachCount()
  .filterValues { it % 2 == 1 }
  .keys

floor.count().let(::println)

val art = generateSequence(0 to floor.associate { it to Color.Black }) { (i, turn) ->
  if (i == 100) {
    null
  } else {
    val coords = turn.keys.flatMap { it.neighbours().plus(it) }.toSet()

    (i + 1) to coords.associate {
      val tile = turn.getOrDefault(it, Color.White)
      val neighbours = it.neighbours().map { turn.getOrDefault(it, Color.White) }
      val black = neighbours.count { it == Color.Black }

      if (tile == Color.Black) {
        if (black == 0 || black > 2) it to Color.White else it to Color.Black
      } else {
        if (black == 2) it to Color.Black else it to Color.White
      }
    }
  }
}.map { it.second }

art.last().count { it.value == Color.Black }.let(::println)
