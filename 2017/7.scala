val pattern = "(\\w+) \\((\\d+)\\)( -> )?(.*)?".r

case class Program(weight: Int, deps: Seq[String])
val programs = io.Source.stdin.getLines.map {
  case pattern(name, weight, _, null) => name -> Program(weight.toInt, Seq.empty)
  case pattern(name, weight, _, deps) => name -> Program(weight.toInt, deps.split(",").map(_.trim).filter(_.nonEmpty).toSeq)
}.toMap

val noDeps = programs.foldLeft(programs.keySet) { case (inDeps, (_, Program(_, deps))) => inDeps -- deps }.head

println(noDeps)

case class Balance(weight: Int, disbalance: Int)
def balance(node: String, nodes: Map[String, Program]): Balance = {
  val current = nodes(node)
  if (current.deps.isEmpty) {
    Balance(current.weight, 0)
  } else {
    val balances = current.deps.map(balance(_, nodes))
    val weights = balances.map(_.weight)
    val disbalances = balances.map(_.disbalance)
    val disbalance = if (disbalances.sum == 0) {
      val diff = weights.max - weights.min
      if (diff > 0) nodes(current.deps(weights.zipWithIndex.maxBy(_._1)._2)).weight - diff else 0
    } else disbalances.sum

    Balance(current.weight + weights.sum, disbalance)
  }
}

println(balance(noDeps, programs).disbalance)