val seq = io.Source.stdin.mkString.trim.split(' ').map(_.toInt)

case class Node(metadata: Seq[Int], children: Node*)

def buildTree(seq: Seq[Int]): Node = {
  def recurse(seq: Seq[Int], offset: Int): (Node, Int) = {
    val numChildren = seq.head
    val metadataCount = seq(1)
    if (numChildren == 0) {
      (Node(seq.slice(2, metadataCount + 2)), 2 + metadataCount)
    } else {
      val (children: List[Node], lastOffset: Int) = (1 to numChildren).foldLeft((List.empty[Node], 2)) {
        case ((list, nextOffset), _) =>
          val (child, length) = recurse(seq.drop(nextOffset), nextOffset)
          (list :+ child, nextOffset + length)
      }

      (Node(seq.slice(lastOffset, lastOffset + metadataCount), children: _*), lastOffset + metadataCount)
    }
  }

  recurse(seq, 0)._1
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