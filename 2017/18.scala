import scala.util.Try

sealed trait Reference
case class Register(name: String) extends Reference
case class Value(value: Long) extends Reference
case object Void extends Reference

case class Op(ins: String, target: String, ref: Reference = Void)

val patternOne = "([a-z]{3}) ([a-z]) ([a-z]|-?\\d+)".r
val patternTwo = "([a-z]{3}) ([a-z])".r
val registers = ('a' to 'z').map { l => l -> 0 }.toMap

val instructions = io.Source.stdin.getLines
  .map {
   case patternOne(ins, target, modifier) => Op(
     ins,
     target,
     Try(modifier.toLong).map(v => Value(v)).getOrElse(Register(modifier))
   )
   case patternTwo(ins, target) => Op(ins, target)
  }
  .toSeq

println(instructions)

instructions.foldLeft(registers) {
  case Op("snd", target,)
}