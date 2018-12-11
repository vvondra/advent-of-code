case class Node(index: Int, x: Int, y: Int)
case class Distance(closest: Int, distance: Int)

val coords = io.Source.stdin.getLines
  .map {
    string =>
      string.split(',')
        .map(_.trim)
        .map(_.toInt)
        .toList match { case List(y, x) => (x, y) }
  }
  .zipWithIndex
  .map { case (pair, i) => Node(i + 1, pair._1, pair._2) }
  .toList

val topLeft = (coords.minBy(_.x).x - 1, coords.minBy(_.y).y - 1)
val bottomRight = (coords.maxBy(_.x).x + 1, coords.maxBy(_.y).y + 1)

val map = Array.fill[Distance](bottomRight._1 - topLeft._1 + 1, bottomRight._2 - topLeft._2 + 1)(Distance(0, Int.MaxValue))

def mapNodes = for {
  x <- 0 to bottomRight._1 - topLeft._1
  y <- 0 to bottomRight._2 - topLeft._2
} yield(x, y)

def distance(node: Node, x: Int, y: Int) = {
  Math.abs(node.x - (x + topLeft._1)) + Math.abs(node.y - (y + topLeft._2))
}

coords.foreach(node =>
  mapNodes.foreach {
    case (x, y) =>
      val best = map(x)(y)
      val dist = distance(node, x, y)
      if (dist < best.distance) {
        map(x)(y) = Distance(node.index, dist)
      } else if (dist == best.distance) {
        map(x)(y) = Distance(0, dist)
      }
  }
)

val biggest = coords
    .filterNot(c => {
      mapNodes.exists {
        case (x, y) => (x == 0 || y == 0 || x == bottomRight._1 - topLeft._1 || y == bottomRight._2 - topLeft._2) && map(x)(y).closest == c.index
      }
    })
    .map(c => {
      mapNodes.count {
        case (x, y) => map(x)(y).closest == c.index
      }
    })
    .max

println(biggest)
