import scala.annotation.tailrec

val lines = io.Source.stdin.getLines
  .zipWithIndex.flatMap { case (r, x) => r.zipWithIndex.map { case (c, y) => (x, y) -> c }}
  .toMap
  .withDefaultValue(' ')

type Pos = (Int, Int)
def travel(tracks: Map[Pos, Char]): (Int, Seq[Char]) = {
  val letterPattern = "([A-Za-z])".r

  val neighbours = Seq((1, 0), (0, 1), (-1, 0), (0, -1))

  def move(current: Pos, prev: Pos): Pos = (current._1  + current._1 - prev._1, current._2  + current._2 - prev._2)

  @tailrec
  def recurse(current: Pos, prev: Pos, letters: Seq[Char], len: Int): (Int, Seq[Char]) = {
    tracks(current) match {
      case '+' => recurse(
        neighbours.map { case (x, y) => (current._1 + x, current._2 + y) }
          .filterNot(_ == current)
          .filterNot(tracks(_) == ' ')
          .filterNot(tracks(_) == tracks(prev))
          .head,
        current,
        letters,
        len + 1
      )
      case letterPattern(l) => recurse(move(current, prev), current, letters :+ l, len + 1)
      case ' ' => (len, letters)
      case _ => recurse(move(current, prev), current, letters, len + 1)
    }
  }

  val start = lines.find { case ((x, _), v) => x == 0 && v != ' '}.head._1
  recurse(start, (start._1 - 1, start._2), Seq.empty, 0)
}

println(travel(lines)._2.mkString)
println(travel(lines)._1)