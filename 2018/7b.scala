import scala.collection.immutable.SortedSet
import scala.collection.mutable

object JobOrder extends Ordering[Job] {
  def compare(x: Job, y: Job) = x.char compare y.char
}

case class Job(char: Char, seconds: Int)
val edges = io.Source.stdin.getLines
  .map(_.split(' '))
  .map(line => (line(1).charAt(0), line(7).charAt(0)))
  .toList

val workers = 5; // 1 for task A
def cost(char: Char) = char - 'A' + 1 + 60 // 1 for task A

val nodes = edges.flatMap(e => Seq(e._1, e._2)).distinct.sorted
var sorted = StringBuilder.newBuilder
var inDegree = mutable.Map(edges.groupBy(_._2).mapValues(_.length).toSeq: _*)
var queue = SortedSet.empty(JobOrder)
var inProgress = SortedSet(nodes.filter(n => inDegree.getOrElse(n, 0) == 0).map(c => Job(c, cost(c))): _*)(JobOrder)

var seconds = 0

while (queue.nonEmpty || inProgress.nonEmpty) {
  val batch = workers - inProgress.size
  inProgress = inProgress ++ queue.take(batch)
  queue = queue.drop(batch)

  while (!inProgress.exists(job => job.seconds == 0)) {
    inProgress = SortedSet(inProgress.map(j => Job(j.char, j.seconds - 1)).toSeq: _*)(JobOrder)
    seconds = seconds + 1
  }

  val n = inProgress.find(job => job.seconds == 0).get
  inProgress = inProgress - n

  sorted.append(n.char)

  edges
    .filter(e => e._1 == n.char)
    .foreach(e => {
      val newInDegree = inDegree(e._2) - 1
      inDegree.put(e._2, newInDegree)

      if (newInDegree == 0) {
        queue = queue + Job(e._2, cost(e._2))
      }
    })
}

println(sorted.result)
println(seconds)