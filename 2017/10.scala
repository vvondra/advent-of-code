def shiftLeft[T](seq: Seq[T], offset: Int): Seq[T] = {
  val shift = offset % seq.length
  seq.drop(shift) ++ seq.take(shift)
}
case class Step(seq: Seq[Int], skipSize: Int, totalShift: Int) {
  def reset = {
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


// Part One
val input = io.Source.stdin.mkString.trim
val lengths = input.split(",").map(_.trim.toInt).toSeq
val hashed = hash(0 to 255, lengths)

println(hashed.reset(0) * hashed.reset(1))

// Part Two
val additionalLengths = Seq(17, 31, 73, 47, 23)
val lengthsTwo = input.toSeq.map(_.toInt) ++ additionalLengths

println(hashRounds(0 to 255, lengthsTwo, 64))