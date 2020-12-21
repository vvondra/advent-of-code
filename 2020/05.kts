import java.io.File

val input = File("5.input").readLines()

fun seatId(code: CharSequence): Int {
  val (rowCode, seatCode) = code.partition { it == 'B' || it == 'F' }

  val row = rowCode.fold(0 to 127) { (min, max), c ->
    when (c) {
      'B' -> (min + max) / 2 + 1 to max
      'F' -> min to (min + max) / 2
      else -> {
        throw Exception("Invalid code")
      }
    }
  }

  val seat = seatCode.fold(0 to 7) { (min, max), c ->
    when (c) {
      'R' -> (min + max) / 2 + 1 to max
      'L' -> min to (min + max) / 2
      else -> {
        throw Exception("Invalid code")
      }
    }
  }

  return row.first * 8 + seat.first
}

val seatCodes = input.map { seatId(it) }

seatCodes.maxOrNull()?.let(::println)

val mySeat = seatCodes.sorted().fold(0) { prev, next ->
  when (prev) {
    0 -> next
    next - 1 -> next
    else -> prev
  }
} + 1

println(mySeat)
