var polymer = io.Source.stdin.mkString.trim

var reduced = false
do {
  val radicals = polymer.sliding(2,1)
    .zipWithIndex
    // Careful, XOR has really weird preference in Scala, lower than ==
    .filter { case (pair, _) => pair.length == 2 && pair.charAt(0).toUpper == pair.charAt(1).toUpper && (pair.charAt(0).isUpper ^ pair.charAt(1).isUpper) }

  val oldLength = polymer.length
  polymer = radicals.foldLeft((polymer, 0, 0)) {
    case ((reducedPolymer, lastIndex, cuts), radical) =>
      if (radical._2 > 1 && radical._2 < lastIndex + 2) {
        (reducedPolymer, lastIndex, cuts)
      } else {
        (reducedPolymer.substring(0, radical._2 - cuts * 2) + reducedPolymer.substring(radical._2 + 2 - cuts * 2), radical._2, cuts + 1)
      }
  }._1

  reduced = polymer.length < oldLength
} while (reduced)

println(polymer)
println(polymer.length)