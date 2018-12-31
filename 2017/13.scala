import scala.collection.immutable.SortedMap

def layerPosition(range: Int, time: Int) = {
  val offset = time % ((range - 1) * 2)

  if (offset > range - 1) {
    2 * (range - 1) - offset // going up
  } else offset // going down
}

val layers = io.Source.stdin.getLines
  .map(_.split(":").map(_.trim))
  .map { case Array(a, b) => (a.toInt, b.toInt) }
  .foldLeft(SortedMap.empty[Int, Int]) { case (m, (index, depth)) => m + (index -> depth) }

def pass(delay: Int = 0) = layers
  .filter { case (d, r) => layerPosition(r, d + delay) == 0 }
  .map { case (d, r) => d * r  }
  .sum


def blocked(delay: Int = 0) = layers.exists { case (d, r) => layerPosition(r, d + delay) == 0 }

println(pass())

val firstToPass = Stream.from(1)
  .dropWhile(blocked)
  .head

println(firstToPass)


