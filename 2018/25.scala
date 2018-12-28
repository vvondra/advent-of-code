import scala.annotation.tailrec

type Coord = (Int, Int, Int, Int, Int)

val coords = io.Source.stdin.getLines
  .map(_.split(",").map(_.trim).map(_.toInt))
  .zipWithIndex
  .map { case (Array(a, b, c, d), i) => i -> (i, a, b, c, d) }
  .toMap

def dist(a: Coord, b: Coord) = Math.abs(a._2 - b._2) + Math.abs(a._3 - b._3) + Math.abs(a._4 - b._4) + Math.abs(a._5 - b._5)

val edges = Array.fill[Boolean](coords.size, coords.size)(false)

coords.values.toSeq.combinations(2).foreach {
  case Seq(u, v) => if (dist(u, v) <= 3) {
    edges(u._1)(v._1) = true
    edges(v._1)(u._1) = true
  }
}

def constellationCount(coordinates: Map[Int, Coord]): Int = {
  @tailrec
  def exploreConstellations(unvisited: Map[Int, Coord], components: Set[Set[Coord]]): Set[Set[Coord]] = {
    if (unvisited.isEmpty) {
      components
    } else {
      val random = unvisited.head
      val component = exploreConstellation(random._2, Set.empty)
      exploreConstellations(unvisited -- component.map(_._1), components + component)
    }
  }

  def exploreConstellation(current: Coord, visited: Set[Coord]): Set[Coord] = {
    if (visited.contains(current))
      visited
    else {
      edges(current._1)
        .zipWithIndex
        .filter(_._1)
        .map(c => coordinates(c._2))
        .filterNot(visited.contains)
        .foldLeft(visited + current)((vis, n) => exploreConstellation(n, vis))
    }
  }

  exploreConstellations(coordinates, Set.empty).size
}

println(constellationCount(coords))