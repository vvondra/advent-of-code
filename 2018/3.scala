case class Cut(id: Int, x: Int, y: Int, width: Int, height: Int)

val pattern = raw"#(\d+) @ (\d+),(\d+): (\d+)x(\d+)".r

val cuts = io.Source.stdin.getLines
  .map {
    case pattern(id, x, y, width, height) => Cut(id.toInt, x.toInt, y.toInt, width.toInt, height.toInt)
  }
  .toList

val applied = cuts.foldLeft(Array.ofDim[Int](1000 , 1000))((matrix, cut) => {
    matrix.zipWithIndex.map {
      case (row, rowNumber) =>
        if (rowNumber >= cut.y && rowNumber < cut.y + cut.height) {
          row.slice(0, cut.x) ++ row.slice(cut.x, cut.x + cut.width).map(_ + 1) ++ row.drop(cut.x + cut.width)
        } else {
          row
        }
    }
  })

println(applied.foldLeft(0)((sum, row) => sum + row.count(_ > 1)))

cuts
  .filter(cut => applied.zipWithIndex.forall {
    case (row, rowNumber) =>
      if (rowNumber >= cut.y && rowNumber < cut.y + cut.height) {
        row.slice(cut.x, cut.x + cut.width).forall(_ == 1)
      } else {
        true
      }
  })
  .foreach(println)