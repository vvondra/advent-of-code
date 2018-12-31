import scala.annotation.tailrec

def jumpOut(instructions: Seq[Int], modifier: Int => Int): Int = {
  @tailrec
  def jump(i: Seq[Int], current: Int, steps: Int): Int = {
    if (!i.indices.contains(current)) {
      steps
    } else {
      jump(i.updated(current, modifier(i(current))), current + i(current), steps + 1)
    }
  }

  jump(instructions, 0, 0)
}

val jumps = io.Source.stdin.getLines.map(_.toInt).toVector

println(jumpOut(jumps, _ + 1))
println(jumpOut(jumps, i => if (i < 3 ) i + 1 else i - 1))
