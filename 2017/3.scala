def distance(input: Int): Int = {
  if (input == 1) {
    return 0
  }

  val sqrtBound = Math.sqrt(input.toDouble).ceil
  val topBound = (if (sqrtBound % 2 == 0) sqrtBound + 1 else sqrtBound).toInt
  val bottomBound = topBound - 2

  val spiralRingLength = (Math.pow(topBound, 2) - Math.pow(bottomBound, 2)).toInt
  val spiralSideLength = (spiralRingLength - 4) / 4 + 1
  val spiralRingOffset = input - Math.pow(bottomBound, 2).toInt
  val sideOffset = spiralRingOffset % spiralSideLength

  val distFromCenter = (bottomBound + 1) / 2
  val distFromSide = Math.abs(spiralSideLength / 2 - sideOffset)

  distFromCenter + distFromSide
}

assert(distance(1) == 0)
assert(distance(1024) == 31)
assert(distance(12) == 3)
assert(distance(23) == 2)

val input = 289326
println(distance(input))

// Cheater cheater :)
val partB = io.Source.fromURL("https://oeis.org/A141481/b141481.txt")
  .getLines
  .filterNot(_.startsWith("#"))
  .map(_.split(" ").map(_.trim).last.toInt)
  .dropWhile(_ < input)
  .next()

println(partB)