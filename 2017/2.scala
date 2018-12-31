val spreadsheet = io.Source.stdin.getLines.map(_.split("\\s+").map(_.toInt).toSeq).toSeq

println(spreadsheet.map { row => row.max - row.min }.sum)

val checksumB = spreadsheet
  .map {row =>
    row.combinations(2)
      .filter { case Seq(a, b) => a % b == 0 || b % a == 0 }
      .map { case Seq(a, b) => (a max b) / (a min b) }
      .toStream.head
  }
  .sum

println(checksumB)