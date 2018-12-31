def generator(seed: Long, factor: Long): Iterator[Long] = new Iterator[Long] {
  var prev: Long = seed
  override def hasNext = true

  override def next(): Long = {
    val next = (prev * factor) % 2147483647
    prev = next
    next
  }
}

def first: Iterator[Long] = generator(722, 16807)
def second: Iterator[Long] = generator(354, 48271)

val mask = (1 << 16) - 1
val test = (a: Long, b: Long) => (a & mask) == (b & mask)

val matching = first.zip(second)
  .take(40 * 1000 * 1000)
  .count(test.tupled)

println(matching)

val seriesB = first.filter(_ % 4 == 0).zip(second.filter(_ % 8 == 0))

val matchingB = seriesB
  .take(5 * 1000 * 1000)
  .count(test.tupled)

println(matchingB)