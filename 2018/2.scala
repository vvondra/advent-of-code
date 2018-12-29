val counts = io.Source.stdin.getLines
  .map(_.toList)
  .map(letters => letters.groupBy(identity).mapValues(_.size))
  .foldLeft((0, 0)) { (sums, counts) => (
    sums._1 + (if (counts.exists(entry => entry._2 == 2)) 1 else 0),
    sums._2 + (if (counts.exists(entry => entry._2 == 3)) 1 else 0),
  )}

println(counts._1 * counts._2)