case class Instruction(code: Opcode, a: Int, b: Int, c: Int)

type Opcode = Int
type Memory = Vector[Opcode]
type Op = (Instruction, Memory) => Memory

case class Sample(before: Memory, ins: Instruction, after: Memory)

val ops = Map[String, Op](
  "addr" -> ((i, m) => m.updated(i.c, m(i.a) + m(i.b))),
  "addi" -> ((i, m) => m.updated(i.c, m(i.a) + i.b)),
  "mulr" -> ((i, m) => m.updated(i.c, m(i.a) * m(i.b))),
  "muli" -> ((i, m) => m.updated(i.c, m(i.a) * i.b)),
  "banr" -> ((i, m) => m.updated(i.c, m(i.a) & m(i.b))),
  "bani" -> ((i, m) => m.updated(i.c, m(i.a) & i.b)),
  "borr" -> ((i, m) => m.updated(i.c, m(i.a) | m(i.b))),
  "bori" -> ((i, m) => m.updated(i.c, m(i.a) | i.b)),

  "setr" -> ((i, m) => m.updated(i.c, m(i.a))),
  "seti" -> ((i, m) => m.updated(i.c, i.a)),

  "gtir" -> ((i, m) => m.updated(i.c, if (i.a > m(i.b)) 1 else 0)),
  "gtri" -> ((i, m) => m.updated(i.c, if (m(i.a) > i.b) 1 else 0)),
  "gtrr" -> ((i, m) => m.updated(i.c, if (m(i.a) > m(i.b)) 1 else 0)),

  "eqir" -> ((i, m) => m.updated(i.c, if (i.a == m(i.b)) 1 else 0)),
  "eqri" -> ((i, m) => m.updated(i.c, if (m(i.a) == i.b) 1 else 0)),
  "eqrr" -> ((i, m) => m.updated(i.c, if (m(i.a) == m(i.b)) 1 else 0))
)

val (samplesDef, testDef) = io.Source.stdin.mkString.split("\n\n\n").map(_.trim) match {
  case Array(s, t) => (s, t)
}

def parseState(s: String) = s.drop(9).dropRight(1).split(',').map(_.trim).map(_.toInt).toVector

val samples = samplesDef.split("\n\n")
  .map(_.split("\n"))
  .map(s => Sample(
    parseState(s(0)),
    s(1).split(' ').map(_.toInt) match { case Array(a, b, c, d) => Instruction(a, b, c, d) },
    parseState(s(2))
  ))

val test = testDef.split("\n")
  .map(_.split(' ').map(_.toInt))
  .map { case Array(a, b, c, d) => Instruction(a, b, c, d) }

val resultA = samples.count(sample => {
  ops.values.count(op => op(sample.ins, sample.before) == sample.after) >= 3
})

println(resultA)

val possible = ops.map { case (k, _) => k -> (0 to 15).toList }

val valid = samples.foldLeft(possible) {
  case (p, sample) =>
    p.map { case (opName, opCodes) =>
      opName -> opCodes.filterNot(opCode => opCode == sample.ins.code && ops(opName)(sample.ins, sample.before) != sample.after)
    }
}

def figureOut(possible: Map[String, List[Opcode]]): Map[Opcode, String] = {
  def recurse(deduced: Map[String, List[Opcode]]): Map[String, List[Opcode]] = {
    val single = deduced.filter(_._2.size == 1)
    val singleVals = single.values.toSeq.flatten
    if (single.size == deduced.size) {
      deduced
    } else {
      recurse(deduced.mapValues(opcodes => {
        if (opcodes.length == 1) {
          opcodes
        } else {
          opcodes.filterNot(o => singleVals.contains(o))
        }
      }))
    }
  }

  recurse(possible).mapValues(_.head).map(_.swap)
}

val opMapping = figureOut(valid)

val resultB = test.foldLeft(Vector(0, 0, 0, 0))((registers, instruction) => ops(opMapping(instruction.code))(instruction, registers))
println(resultB)
