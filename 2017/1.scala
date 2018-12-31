val s = io.Source.stdin.mkString
  .toSeq
  .map(_.toInt - '0')

val partial = s.sliding(2)
  .map { case Seq(a, b) => if (a == b) a else 0 }
  .sum
println(partial + (if (s.head == s.last) s.head else 0))

println(s.zipWithIndex.map { case (c, i) => if (c == s((i + s.length / 2) % s.length)) c else 0 }.sum)