import scala.annotation.tailrec

val banks = io.Source.stdin.mkString.split("\\s+").map(_.toInt).toVector

def redistribute(banks: Seq[Int]): Seq[Int] = {
  @tailrec
  def redistribute(banks: Seq[Int], remaining: Int, current: Int): Seq[Int] = {
    if (remaining == 0) {
      banks
    } else {
      redistribute(banks.updated(current, banks(current) + 1), remaining - 1, (current + 1) % banks.length)
    }
  }

  val largest = banks.zipWithIndex.maxBy(_._1)
  redistribute(banks.updated(largest._2, 0), largest._1, (largest._2 + 1) % banks.length)
}


def detectLoop(banks: Seq[Int]): (Int, Int) = {
  @tailrec
  def innerLoop(banks: Seq[Int], seen: Map[Seq[Int], Int], times: Int): (Int, Int) = {
    if (seen.contains(banks)) {
      (times, times - seen(banks))
    } else {
      innerLoop(redistribute(banks), seen + (banks -> times), times + 1)
    }
  }

  innerLoop(banks, Map.empty, 0)
}

println(detectLoop(banks))