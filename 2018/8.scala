import scala.collection.immutable.Queue

val seq = io.Source.stdin.mkString.trim.split(' ').map(_.toInt)

case class Node(metadata: Seq[Int], children: Node*)

def buildTree(seq: Seq[Int]): Node = {
  def recurse(seq: Queue[Int]): (Node, Queue[Int]) = {
    val (numChildren, seq2) = seq.dequeue
    val (metadataCount, seqChildren) = seq2.dequeue
    if (numChildren == 0) {
      (Node(seqChildren.take(metadataCount)), seqChildren.drop(metadataCount))
    } else {
      val (children: List[Node], tailSeq: Queue[Int]) = (1 to numChildren).foldLeft((List.empty[Node], seqChildren)) {
        case ((list, subSeq), _) =>
          val (child, restSeq) = recurse(subSeq)
          (list :+ child, restSeq)
      }

      (Node(tailSeq.take(metadataCount), children: _*), tailSeq.drop(metadataCount))
    }
  }

  recurse(Queue(seq: _*))._1
}

def printTree(node: Node): Unit = {
  def print(node: Node, depth: Int): Unit = {
    println((" " * depth) + node.metadata.mkString(","))
    node.children.foreach(print(_, depth + 1))
  }

  print(node, 0)
}

def checksum(node: Node): Int = node.metadata.sum + node.children.map(checksum).sum
def checksum2(node: Node): Int = {
  if (node.children.isEmpty) {
    node.metadata.sum
  } else {
    node.metadata.map(i => node.children.lift(i - 1).map(checksum2).getOrElse(0)).sum
  }
}


val tree = buildTree(seq)
println(checksum(tree))
println(checksum2(tree))