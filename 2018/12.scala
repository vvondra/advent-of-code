import scala.annotation.tailrec

val in = io.Source.stdin.getLines.toVector

case class Row(line: String, start: Long)
case class Next(row: Row, diff: Long, times: Long)
val initial = Row(in.head.split(' ').last, 0)
val rules = in.drop(2).map(_.split(" => ")).map { case Array(a, b) => a -> b }.toMap

def transform(line: String) = line.take(2) + line
  .sliding(5, 1)
  .map(seg => rules.getOrElse(seg, "."))
  .mkString("") + line.takeRight(2)

def score(row: Row): Long = row.line.zipWithIndex.map { case (c, i) =>
  if (c == '#') i + row.start
  else 0
}.sum

def score(next: Next): Long = score(next.row) + next.times * next.diff

def repeat(row: Row, times: Long): Next = {
  @tailrec
  def recurse(row: Row, remaining: Long, prevDiff: Long): Next = {
    val prePadded = if (row.line.startsWith(".....")) row else Row("....." + row.line, row.start - 5)
    val postPadded = if (prePadded.line.endsWith(".....")) prePadded else Row(prePadded.line + ".....", prePadded.start)
    val transformed = Row(transform(postPadded.line), postPadded.start)
    val diff = score(transformed) - score(row)

    if (transformed == row || remaining == 0 || (prevDiff == diff && "." + row.line.dropRight(1) == transformed.line)) {
      Next(transformed, diff, remaining)
    } else {
      recurse(transformed, remaining - 1, diff)
    }
  }

  recurse(row, times - 1, 0)
}

val transformed = repeat(initial, 50000000000L)
println(transformed)
println(score(transformed.row))
println(score(transformed))