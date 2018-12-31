case class Cube(x: Int, y: Int, z: Int) {
  // Huge thanks for insight from https://www.redblobgames.com/grids/hexagons/
  def +(other: Cube): Cube = Cube(x + other.x, y + other.y, z + other.z)
  def distanceTo(other: Cube): Int = (Math.abs(x - other.x) + Math.abs(y - other.y) + Math.abs(z - other.z)) / 2
  def compassMove(dir: String) = dir match {
    case "n" => this + Cube(0, 1, -1)
    case "ne" => this + Cube(1, 0, -1)
    case "se" => this + Cube(1, -1, 0)
    case "s" => this + Cube(0, -1, 1)
    case "sw" => this + Cube(-1, 0, 1)
    case "nw" => this + Cube(-1, 1, 0)
  }
}

val moves = io.Source.stdin.mkString.split(",").toSeq

val start = Cube(0, 0, 0)
val (max, dest) = moves.foldLeft((0, start)) {
  case ((furthest, cube), move) =>
    val next = cube.compassMove(move)
    (Math.max(furthest, next.distanceTo(start)), next)
}

println(start.distanceTo(dest))
println(max)

