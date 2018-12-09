import scala.collection.mutable

object MinOrder extends Ordering[String] {
  def compare(x: String, y: String) = y compare x
}

val edges = io.Source.stdin.getLines
  .map(_.split(' '))
  .map(line => (line(1), line(7)))
  .toList

val nodes = edges.flatMap(e => Seq(e._1, e._2)).distinct.sorted
var sorted = mutable.ListBuffer[String]()
var inDegree = mutable.Map(edges.groupBy(_._2).mapValues(_.length).toSeq: _*)
var queue = mutable.PriorityQueue(nodes.filter(n => inDegree.getOrElse(n, 0) == 0): _*)(MinOrder)

while (queue.nonEmpty) {
  val n = queue.dequeue()
  sorted += n

  edges
    .filter(e => e._1 == n)
    .foreach(e => {
      val newInDegree = inDegree(e._2) - 1
      inDegree.put(e._2, newInDegree)

      if (newInDegree == 0) {
        queue.enqueue(e._2)
      }
    })
}

println(sorted.mkString(""))