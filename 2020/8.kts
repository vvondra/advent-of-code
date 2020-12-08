import java.io.File

data class Ins(val code: String, val arg: Int)
data class State(val ip: Int, val acc: Int)

var instructions = File("8.input").readLines()
  .map { it.split(" ").let { Ins(it[0], it[1].toInt()) } }

fun getProgram(ins: List<Ins>): Sequence<State> {
  return generateSequence(Pair(State(0, 0), emptySet<Int>()), exec@{ (state, visited) ->
    val (ip, acc) = state
    if (ip >= ins.size) return@exec null // Termination detection

    val next = when (ins[ip].code) {
      "nop" -> State(ip + 1, acc)
      "acc" -> State(ip + 1, acc + ins[ip].arg)
      "jmp" -> State(ip + ins[ip].arg, acc)
      else -> {
        throw Exception("Unknown instruction")
      }
    }

    // Infinite loop detector
    if (visited.contains(next.ip)) null else Pair(next, visited.plus(next.ip))
  }).map { it.first }
}

// Part 1
getProgram(instructions).last().let { println(it.acc) }


// Part 2
val fixes = instructions.mapIndexed { i, ins ->
  when (ins.code) {
    "acc" -> null
    "jmp" -> instructions.take(i) + ins.copy(code = "nop") + instructions.drop(i + 1)
    "nop" -> instructions.take(i) + ins.copy(code = "jmp") + instructions.drop(i + 1)
    else -> null
  }
}.filterNotNull()

val fixed = fixes
  .map { getProgram(it).last() }
  .find { it.ip == instructions.size }
  ?.let { println(it.acc) }
