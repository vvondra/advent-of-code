val serial = 7689
val dim = 300
def grid = for {
  x <- 1 to dim
  y <- 1 to dim
} yield (x, y)

var powerLevels = grid
  .map { case (x, y) => (x, y) -> ((((((x + 10) * y) + serial) * (x + 10)) / 100 % 10) - 5) }
  .toMap
val powerMap = Array.fill[Int](dim + 1, dim + 1)(0)
powerLevels.foreach { case ((i, j), v) => powerMap(i)(j) = v }

case class Best(best: Int, x: Int, y: Int, size: Int) {
  def answer = s"$x,$y,$size"
}

def maxSubMatrix(m: Array[Array[Int]]) = {
  val rows = m.length
  val cols = m.head.length

  // Pre-calculate all cumulative sums for each column in original matrix
  val partialSums = Array.fill(rows, cols)(0)
  m.head.copyToArray(partialSums(0))
  for {
    j <- 1 until cols
    i <- 1 until rows
  } {
    partialSums(i)(j) = m(i)(j) + partialSums(i - 1)(j) + partialSums(i)(j - 1) - partialSums(i - 1)(j - 1)
  }

  var best, bi, bj, bs = Int.MinValue
  for {
    s <- 1 until rows
    i <- s until rows
    j <- s until rows
  } {
    val total = partialSums(i)(j) - partialSums(i - s)(j) - partialSums(i)(j - s) + partialSums(i - s)(j - s)
    if (total > best) {
      best = total
      bi = i
      bj = j
      bs = s
    }
  }

  Best(best, bi - bs + 1, bj - bs + 1, bs)
}

println(maxSubMatrix(powerMap).answer)
