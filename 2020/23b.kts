val input = "523764819".map { Character.getNumericValue(it) }
val max = input.maxOrNull()!!

val limit = 1_000_000
val nums = mutableMapOf<Int, Int>()
input.zipWithNext().forEach { (a, b) -> nums[a] = b }
nums[input.last()] = max + 1
(max + 1..limit).forEach { nums[it] = it + 1 }
nums[limit] = input.first()

fun takeThree(current: Int): List<Int> = listOf(
  nums[current]!!,
  nums[nums[current]!!]!!,
  nums[nums[nums[current]!!]!!]!!
)

fun destination(current: Int, pickedUp: List<Int>): Int {
  var next = current - 1
  next = if (next < 1) limit else next
  while (next in pickedUp) {
    next = next - 1
    next = if (next < 1) limit else next
  }
  return next
}

fun printNums(): Long {
  val a = nums[1]!!
  val b = nums[nums[1]!!]!!

  return a.toLong() * b.toLong()
}

var i = 0
var current = input.first()
while (i++ < 10_000_000) {
  val pickedUp = takeThree(current)
  val dest = destination(current, pickedUp)
  val aux = nums[dest]!!
  nums[current] = nums[pickedUp[2]]!!
  nums[dest] = pickedUp[0]
  nums[pickedUp[2]] = aux
  current = nums[current]!!
}

println(printNums())
