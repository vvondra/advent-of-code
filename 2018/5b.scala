var polymer = io.Source.stdin.mkString.trim

val mostEfficient = ('a' until 'z')
  .par
  .map(char => {
    var reduced = false
    var letterPolymer = polymer.replaceAll("(?i)" + char, "")
    do {
      val radicals = letterPolymer.sliding(2,1)
        .zipWithIndex
        // Careful, XOR has really weird preference in Scala, lower than ==
        .filter { case (pair, _) => pair.length == 2 && pair.charAt(0).toUpper == pair.charAt(1).toUpper && (pair.charAt(0).isUpper ^ pair.charAt(1).isUpper) }

      val oldLength = letterPolymer.length
      letterPolymer = radicals.foldLeft((letterPolymer, 0, 0)) {
        case ((reducedPolymer, lastIndex, cuts), radical) =>
          if (radical._2 > 1 && radical._2 < lastIndex + 2) {
            (reducedPolymer, lastIndex, cuts)
          } else {
            (reducedPolymer.substring(0, radical._2 - cuts * 2) + reducedPolymer.substring(radical._2 + 2 - cuts * 2), radical._2, cuts + 1)
          }
      }._1

      reduced = letterPolymer.length < oldLength
    } while (reduced)

    println(char, letterPolymer.length)

    (char, letterPolymer.length)
  })
  .minBy(_._2)


println(mostEfficient)
