import java.io.File

val nums = File("9.input").readLines().map(String::toLong)
val windowLength = 25

val firstInvalid = nums.windowed(windowLength + 1)
  .find { window ->
    val preamble = window.take(windowLength).toSet()
    !preamble.any { i -> preamble.minus(i).contains(window.last() - i) }
  }!!
  .let { it.last() }

println(firstInvalid)

// Part two, solution A, recalculating sum starting from each index
nums.indices
  .map { i -> nums.drop(i) }
  .map {
    var sum: Long = 0
    it.takeWhile {
      sum += it
      sum <= firstInvalid
    }
  }
  .filter { it.sum() == firstInvalid }
  .first()
  .let { (it.minOrNull() ?: 0) + (it.maxOrNull() ?: 0) }
  .let(::println)

// Part two, solution B, sliding a time window until the sum is just right
generateSequence(Triple(0, 0, 0L)) { (i, j, sum) ->
  val diff = firstInvalid - sum
  when {
    diff > 0L -> Triple(i, j + 1, sum + nums[j])
    diff < 0L -> Triple(i + 1, j, sum - nums[i])
    else -> null
  }
}
  .last()
  .let { (i, j, _) -> nums.subList(i, j) }
  .let { (it.minOrNull() ?: 0) + (it.maxOrNull() ?: 0) }
  .let(::println)
