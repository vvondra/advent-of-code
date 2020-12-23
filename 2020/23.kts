val input = "523764819".map { Character.getNumericValue(it) }

val nums = mutableMapOf<Int, Int>()
input.zipWithNext().forEach { (a, b) ->
  nums[a] = b
}
nums[input.last()] = input.first()
val max = input.maxOrNull()!!

fun takeThree(current: Int): List<Int> = listOf(
  nums[current]!!,
  nums[nums[current]!!]!!,
  nums[nums[nums[current]!!]!!]!!
)

fun destination(current: Int, pickedUp: List<Int>): Int {
  var next = current - 1
  next = if (next < 1) max else next
  while (next in pickedUp) {
    next = next - 1
    next = if (next < 1) max else next
  }
  return next
}

fun printNums(): String {
  var num = nums[1]!!
  val sb = StringBuilder()
  while (num != 1) {
    sb.append(num)
    num = nums[num]!!
  }
  return sb.toString()
}

var i = 0
var current = input.first()
while (i++ < 100) {
  val pickedUp = takeThree(current)
  val dest = destination(current, pickedUp)
  val aux = nums[dest]!!
  nums[current] = nums[pickedUp[2]]!!
  nums[dest] = pickedUp[0]
  nums[pickedUp[2]] = aux
  current = nums[current]!!
}

println(printNums())
