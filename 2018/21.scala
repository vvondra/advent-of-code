import scala.annotation.tailrec
import scala.collection.immutable.Queue

case class Instruction(code: OpName, a: Int, b: Int, c: Int)

type OpName = String
type Memory = Vector[Int]
type Op = (Instruction, Memory) => Memory


val ops = Map[OpName, Op](
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

def execute(initial: Memory, ip: Int, instructions: Seq[Instruction]): Memory = {
  @tailrec
  def step(state: Memory, comparisons: Set[Int], prev: Int): Memory = {
    val instruction = instructions(state(ip))
    val next = ops(instruction.code)(instruction, state)

    val nextIp = next(ip) + 1

    // Solution to Part 1, this is the comparison triggering the end condition
    if (nextIp == 28) {
      if (comparisons.contains(next(1))) {
        println(prev)
      }
    }

    if (!instructions.indices.contains(nextIp)) {
      next
    } else {
      step(next.updated(ip, nextIp), if (nextIp == 28) comparisons + next(1) else comparisons, if (nextIp == 28) next(1) else prev)
    }
  }

  step(initial, Set.empty, 0)
}

val (ipDef, instructionsDef) = Queue(io.Source.stdin.getLines.toSeq: _*).dequeue
val ip = ipDef.split(' ').last.toInt
val instructions = instructionsDef.map(_.split(' ')).map { case Array(i, a, b, c) => Instruction(i, a.toInt, b.toInt, c.toInt) }.toVector

println(ip)
println(instructions)

println(execute(Vector(0, 0, 0, 0, 0, 0), ip, instructions))
