def shiftLeft[T](seq: Seq[T], offset: Int): Seq[T] = {
  val shift = offset % seq.length
  seq.drop(shift) ++ seq.take(shift)
}

val input = 371
val buffer: Seq[Int] = Vector(0)
var range = 1 to 2017

val result = range.foldLeft(buffer) { case (b, i) => shiftLeft(b :+ i, input) }

println(result(result.indexOf(2017) + 1))

val bufferTwo: Seq[Int] = Vector(0)
var rangeTwo = 1 to (50 * 1000 * 1000)

val resultTwo = rangeTwo.foldLeft((0, 0)) {
  case ((afterZero, current), i) =>
    val next = ((current + input) % i) + 1
    if (next == 1) {
      (i, next)
    } else {
      (afterZero, next)
    }
}

println(resultTwo._1)