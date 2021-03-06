val words = io.Source.stdin.getLines
  .map(_.toList)
  .toList

(for (x <- words; y <- words) yield x zip y)
  .find(lists => lists.count(pair => pair._1 != pair._2) == 1) // Filter words with exactly one different letter
  .map(chars => chars.filter(pair => pair._1 == pair._2)) // Take letters which are the same
  .foreach(chars => chars.foreach(pair => print(pair._1))) // Print the common letters
