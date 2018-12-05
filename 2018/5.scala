var polymer = io.Source.stdin.mkString.trim

val mostEfficient = (('a' until 'z').toList ++ List(' '))
  .par
  .map(char => {
    var reduced = false
    // Find solutions for B
    var letterPolymer = polymer.replaceAll("(?i)" + char, "")
    do {
      // Find opposite polarity pairs, return start index in origianl string
      val radicals = letterPolymer.sliding(2,1)
        .zipWithIndex
        // Careful, XOR has really weird preference in Scala, lower than ==
        .filter { case (pair, _) => pair.length == 2 && pair.charAt(0).toUpper == pair.charAt(1).toUpper && (pair.charAt(0).isUpper ^ pair.charAt(1).isUpper) }

      val oldLength = letterPolymer.length

      letterPolymer = radicals.foldLeft((letterPolymer, 0, 0)) {
        case ((reducedPolymer, lastIndex, cuts), radical) =>
          // If a pair such as aAa occurs, the sliding window would cut too many characters at once
          // Skip the second pair and wait for another pass
          if (radical._2 > 1 && radical._2 < lastIndex + 2) {
            (reducedPolymer, lastIndex, cuts)
          } else {
            // The substring index has to be adjusted by already cut pairs in the previous runs
            (reducedPolymer.substring(0, radical._2 - cuts * 2) + reducedPolymer.substring(radical._2 + 2 - cuts * 2), radical._2, cuts + 1)
          }
      }._1

      reduced = letterPolymer.length < oldLength
    } while (reduced)

    // Progress bar :)
    println(char, letterPolymer.length)

    (char, letterPolymer.length)
  })
  .minBy(_._2) // Solution for task A is under the tuple with space as the first element


println(mostEfficient)
