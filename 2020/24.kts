import java.io.File

data class Hex(val q: Int, val r: Int) {

  fun move(dir: String): Hex {
    val offset = axialDirections.get(dir)!!

    return Hex(q + offset.q, r + offset.r)
  }

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

File("24.input").readLines()
  .map(::parse)
  .map { it.fold(Hex(0, 0), Hex::move) }
  .groupingBy { it }
  .eachCount()
  .filterValues { it % 2 == 1 }
  .count()
  .let(::println)
