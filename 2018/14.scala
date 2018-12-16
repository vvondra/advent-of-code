val input = 360781
val inputString = input.toString

class Recipe(var next: Recipe, val value: Int) {
  override def toString: String = s"R($value)"
}

object Recipe {
  def apply(a0: => Recipe, b: Int) = new Recipe(a0, b)
}

var first: Recipe = Recipe(null, 3)
var second: Recipe = Recipe(first, 7)
first.next = second

var last = second
val start = first

lazy val scoreboard: Stream[Recipe] = start #:: scoreboard.map(_.next)

for (_ <- 1 to 50 * input) {
  val sum = first.value + second.value
  val digit = sum % 10
  val tensDigit = (sum / 10) % 10

  if (tensDigit > 0) {
    val secondNew = Recipe(start, tensDigit)
    last.next = secondNew
    last = secondNew
  }

  val firstNew = Recipe(start, digit)
  last.next = firstNew
  last = firstNew

  for (_ <- 0 until first.value + 1) {
    first = first.next
  }

  for (_ <- 0 until second.value + 1) {
    second = second.next
  }
}

scoreboard.slice(input, input + 10).foreach(r => print(r.value))
println

val resultB = scoreboard
  .zipWithIndex
  .sliding(inputString.length, 1)
  .dropWhile(window => window.map(_._1.value).mkString("") != inputString)
  .take(1)
  .toStream
  .head
  .head

println(resultB._2)

