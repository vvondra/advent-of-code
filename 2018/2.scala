val counts = io.Source.stdin.getLines
  .map(_.toList)
  .map(letters => letters.foldLeft[Map[Char, Int]](Map.empty)((m, c) => m + (c -> (m.getOrElse(c, 0) + 1))))
  .foldLeft((0,0))((sums: (Int, Int), counts: Map[Char, Int]) => (
    sums._1 + (if (counts.exists(entry => entry._2 == 2)) 1 else 0),
    sums._2 + (if (counts.exists(entry => entry._2 == 3)) 1 else 0),
  ))

println(counts._1 * counts._2)