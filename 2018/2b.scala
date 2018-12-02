implicit class Crossable[X](xs: Traversable[X]) {
  def cross[Y](ys: Traversable[Y]) = for { x <- xs; y <- ys} yield (x, y)
}

val words = io.Source.stdin.getLines
  .map(_.toList)
  .toList

(words cross words)
  .map(lists => lists._1 zip lists._2) // Zip up letters at same position
  .find(lists => lists.count(pair => pair._1 != pair._2) == 1) // Filter words with exactly one different letter
  .map(chars => chars.filter(pair => pair._1 == pair._2)) // Take letters which are the same
  .foreach(chars => chars.foreach(pair => print(pair._1))) // Print the common letters