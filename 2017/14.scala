import scala.annotation.tailrec

val seed = "stpzcrnm"
val rows = 0 until 128

def shiftLeft[T](seq: Seq[T], offset: Int): Seq[T] = {
  val shift = offset % seq.length
  seq.drop(shift) ++ seq.take(shift)
}
case class Step(seq: Seq[Int], skipSize: Int, totalShift: Int) {
  def reset: Seq[Int] = {
    val revShift = Math.floorMod(seq.size - totalShift, seq.size)
    seq.drop(revShift) ++ seq.take(revShift)
  }
}

def hash(seq: Seq[Int], lengths: Seq[Int], initialSkip: Int = 0, initialShift: Int = 0): Step = {
  lengths.foldLeft(Step(seq, initialSkip, initialShift)) {
    case (Step(s, skipSize, shift), length) =>
      Step(
        shiftLeft(s.take(length).reverse ++ s.drop(length), length + skipSize),
        skipSize + 1,
        shift + length + skipSize
      )
  }
}

def hashRounds(seq: Seq[Int], lengths: Seq[Int], rounds: Int): String = {
  (1 to rounds)
    .foldLeft(Step(seq, 0, 0)) {
      case (step, _) => hash(step.seq, lengths, step.skipSize, step.totalShift)
    }
    .reset
    .grouped(16)
    .map(_.reduce(_ ^ _))
    .map(_.toHexString.reverse.padTo(2, '0').reverse)
    .mkString
}

def knotHash(input: String): String = {
  hashRounds(0 to 255, input.toSeq.map(_.toInt) ++ Seq(17, 31, 73, 47, 23), 64)
}

def bitKnotHash(input: String): String = BigInt(knotHash(input), 16).toString(2).reverse.padTo(128, '0').reverse

// Part A
val hashes = rows.map { r => s"$seed-$r" }.map(bitKnotHash).map(_.toSeq)
println(hashes.map(_.count(_ == '1')).sum)

type Pos = (Int, Int)
// Part B
def componentCount(bits: Seq[Seq[Char]]): Int = {
  @tailrec
  def exploreComponents(unvisited: Set[Pos], components: Set[Set[Pos]]): Set[Set[Pos]] = {
    if (unvisited.isEmpty) {
      components
    } else {
      val c = component(unvisited.head, bits)
      exploreComponents(unvisited -- c, components + c)
    }
  }

  exploreComponents((for (i <- rows; j <- rows if bits(i)(j) == '1') yield (i, j)).toSet, Set.empty).size
}

def component(current: Pos, bits: Seq[Seq[Char]]): Set[Pos] = {
  def recurse(current: Pos, visited: Set[Pos]): Set[Pos] = {
    if (visited.contains(current))
      visited
    else {
      neighbours(current, bits)
        .diff(visited)
        .foldLeft(visited + current)((vis, n) => recurse(n, vis))
    }
  }

  recurse(current, Set.empty)
}

def neighbours(current: Pos, connections: Seq[Seq[Char]]): Set[Pos] = {
  Seq((0, 1), (1, 0), (-1, 0), (0, -1))
    .map { case (dx, dy) => (current._1 + dx, current._2 + dy ) }
    .filter { case (x, y) => rows.contains(x) && rows.contains(y) }
    .filter { case (x, y) => connections(x)(y) == '1'}
    .toSet
}

println(componentCount(hashes))