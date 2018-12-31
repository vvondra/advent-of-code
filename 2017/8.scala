val pattern = "(\\w+) (inc|dec) (-?\\d+) if (\\w+) ([=<>!]+) (-?\\d+)".r

case class Instruction(source: String, op: String, change: Int, condSource: String, condOp: String, condVal: Int)

val instructions = io.Source.stdin.getLines.map {
  case pattern(source, op, change, condSource, condOp, condVal) =>
    Instruction(source, op, change.toInt, condSource, condOp, condVal.toInt)
}.toSeq

val predicates = Map[String, (Int, Int) => Boolean](
  "==" -> ((a, b) => a == b),
  "!=" -> ((a, b) => a != b),
  ">=" -> ((a, b) => a >= b),
  "<=" -> ((a, b) => a <= b),
  ">" -> ((a, b) => a > b),
  "<" -> ((a, b) => a < b)
)
val operators = Map[String, (Int, Int) => Int](
  "inc" -> ((a, b) => a + b),
  "dec" -> ((a, b) => a - b)
)

val result = instructions.foldLeft(Map[String, Int]().withDefaultValue(0), Int.MinValue) {
  case ((registers, max), Instruction(source, op, change, condSource, condOp, condVal)) =>
    if (predicates(condOp)(registers(condSource), condVal)) {
      val updated = registers + (source -> operators(op)(registers(source), change))
      (updated, max max updated.values.max)
    } else (registers, max)
}

println(result._1.values.max)
println(result._2)