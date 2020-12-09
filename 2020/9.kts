import java.io.File

val nums = File("9.input").readLines().map(String::toLong)
val preamble = 25

val firstInvalid = nums.windowed(preamble + 1)
  .map { window ->
    val preamble = window.take(preamble)
    val head = window.last()

    Pair(
      head,
      preamble.withIndex().any { (i, first) -> preamble.drop(i).any { second -> first + second == head } }
    )
  }
  .find { (_, valid) -> !valid }!!
  .let { it.first }

println(firstInvalid)

nums.indices
  .map { i -> nums.drop(i) }
  .map {
    var sum: Long = 0;
    it.takeWhile {
      sum += it
      sum <= firstInvalid
    }
  }
  .filter { it.sum() == firstInvalid }
  .first()
  .let { (it.minOrNull() ?: 0) + (it.maxOrNull() ?: 0) }
  .let(::println)
