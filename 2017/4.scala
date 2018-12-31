val phrases = io.Source.stdin.getLines.toSeq

val validIdentical = phrases
  .map(_.split(" ").groupBy(identity).forall { case (_, p) => p.length == 1})
  .count(identity)

val validAnagram = phrases
  .map(_.split(" ").map(_.groupBy(identity)).groupBy(identity).forall { case (_, p) => p.length == 1} )
  .count(identity)

println(validIdentical)
println(validAnagram)