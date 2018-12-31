import scala.annotation.tailrec

def shiftRight[T](seq: Seq[T], offset: Int): Seq[T] = {
  val shift = (seq.length - offset) % seq.length
  seq.drop(shift) ++ seq.take(shift)
}


sealed trait Move {
  def move(p: Seq[Char]): Seq[Char]
}
case class Spin(shift: Int) extends Move {
  override def move(p: Seq[Char]) = shiftRight(p, shift)
}
case class Exchange(a: Int, b: Int) extends Move {
  override def move(p: Seq[Char]) = p.updated(a, p(b)).updated(b, p(a))
}
case class Partner(a: Char, b: Char) extends Move {
  override def move(p: Seq[Char]) = p.updated(p.indexOf(a), b).updated(p.indexOf(b), a)
}

val exchange = "x(\\d+)/(\\d+)".r
val partner = "p(\\w+)/(\\w+)".r
val spin = "s(\\d+)".r

val moves = io.Source.stdin.mkString.split(',').map {
  case exchange(a, b) => Exchange(a.toInt, b.toInt)
  case spin(a) => Spin(a.toInt)
  case partner(a, b) => Partner(a.head, b.head)
}.toSeq

val programs: Seq[Char] = 'a' to 'p'

def singleDance(positions: Seq[Char]): Seq[Char] = moves.foldLeft(positions) { case (p, m) => m.move(p) }
println(singleDance(programs).mkString)


def dance(positions: Seq[Char], times: Int): Seq[Char] = {
  @tailrec
  def recurse(positions: Seq[Char], seen: Map[Seq[Char], Int], times: Int): Seq[Char] = {
    if (times == 0) {
      positions
    } else {
      val next = singleDance(positions)
      recurse(
        next,
        seen + (next -> times),
        seen.get(next).map(prev => (times % (prev - times)) - 1).getOrElse(times - 1)
      )
    }
  }

  recurse(positions, Map.empty, times)
}

println(dance(programs, 1000 * 1000 * 1000).mkString)