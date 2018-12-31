case class Pos(x: Int, y: Int, z: Int) {
  def +(other: Pos) = Pos(x + other.x, y + other.y, z + other.z)
}
case class P(p: Pos, v: Pos, a: Pos)

val particles = io.Source.stdin.getLines
  .map(
    _.split(", ")
      .map(s => s.substring(3, s.length - 1).split(",").map(_.toInt))
      .map { case Array(x, y, z) => Pos(x, y, z) }
  )
  .map { case Array(p, v, a) => P(p, v, a) }
  .toSeq

val minByAcceleration = particles
  .zipWithIndex
  .minBy { case (P(_, _ , a), _) => Math.abs(a.x) + Math.abs(a.y) + Math.abs(a.z) }

println(minByAcceleration._2) // i was lucky on my inputs


def simulate(particles: Seq[P], prev: Int): Unit = {
  val next = particles.map { case P(p, v, a) => P(p + v + a, v + a, a) }.groupBy(_.p).filter { case(_, ps) => ps.length == 1 }.values.flatten.toSeq
  if (prev != next.size) { println(next.size) }
  simulate(next, next.size)
}

simulate(particles, 0)