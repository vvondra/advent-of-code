import java.io.File

data class OpResult(val state: List<Long>, val ip: Long, val rp: Long, val outputs: List<Long> = emptyList())
enum class Mode {
  Position,
  Immediate,
  Relative
}

class Op(val Int: Long, val ip: Int, prev: List<Long>) {
  val op: Long;
  val modes: List<Long>
  val params: List<Long>
  init {
    val code = prev[ip]
    op = code % 100
    modes = (code / 100).toString(radix = 10).map { Character.getNumericValue(it).toLong() }.reversed()
    params = prev.subList(ip + 1, ip + 1 + opParamCount(op))
  }

  fun nextStep(): Long = ip + paramCount() + 1
  fun paramCount(): Long = opParamCount(op)
  fun value(position: Long, state: List<Long>, writeMode: Boolean = false): Long =
    when (Mode.values().get(modes.getOrElse(position, { 0 }))) {
      Mode.Position -> if (writeMode) params[position]
                          else state.getOrElse(params[position]) { 0 }
      Mode.Immediate -> params[position]
      Mode.Relative -> if (writeMode) params[position] + rp
                          else state.getOrElse(params[position] + rp) { 0 }
    }

  fun execute(state: List<Long>, inputs: () -> Long): OpResult {
    val default = OpResult(state, nextStep(), rp)

    fun updated(memory: List<Long>, idx: Long, value: Long): List<Long> =
      memory.toMutableList().apply {
        set(value(idx, this, true), value)
      }.toList()

    return when (op) {
      1 -> default.copy(state = updated(state, 2, value(0, state) + value(1, state)))
      2 -> default.copy(state = updated(state, 2, value(0, state) * value(1, state)))
      3 -> default.copy(state = updated(state, 0, inputs()))
      4 -> default.copy(outputs = listOf(value(0, state)))
      5 -> if (value(0, state) != 0) default.copy(ip = value(1, state)) else default
      6 -> if (value(0, state) == 0) default.copy(ip = value(1, state)) else default
      7 -> default.copy(state = updated(state, 2, if (value(0, state) < value(1, state)) 1 else 0))
      8 -> default.copy(state = updated(state, 2, if (value(0, state) == value(1, state)) 1 else 0))
      9 -> default.copy(rp = default.rp + value(0, state))
      99 -> default.copy(ip = ip)
      else -> throw Exception("What is $op?")
    }
  }

  companion object {
    fun opParamCount(op: Long): Long =
      mapOf(1 to 3, 2 to 3, 3 to 1, 4 to 1, 5 to 2, 6 to 2, 7 to 3, 8 to 3, 9 to 1, 99 to 0)
        .get(op)!!
  }
}

class Program(var state: List<Long>, inputSeed: List<Long> = emptyList()) {

  var ip = 0
  var rp = 0
  var inputFn = fun(): Long = inputs.removeFirst()
  val inputs: MutableList<Long>

  init {
    inputs = inputSeed.toMutableList()
  }

  fun addInput(new: List<Long>) {
    this.inputs.addAll(new)
  }

  fun clone(): Program = Program(state, inputs).also {
    it.rp = this.rp
    it.ip = this.ip
  }

  fun run(): Sequence<Long> {
    return sequence {
      while (state[ip] != 99L) {
        Op(rp, ip, state).execute(state, inputFn).let {
          ip = it.ip
          rp = it.rp
          state = it.state

          it.outputs.forEach { yield(it) }
        }
      }
    }
  }
}

val inputA = File("../2.input").readText().split(",").map(String::trim).map(String::toLong).toMutableList().apply {
  set(1, 12)
  set(2, 2)
}.toList()
Program(inputA).run().forEach(::println)

val inputB = File("../5.input").readText().split(",").map(String::trim).map(String::toLong)
Program(inputB, listOf(1)).run().forEach { println(it) }
Program(inputB, listOf(5)).run().last().let { println(it) }

val inputC = File("../9.input").readText().split(",").map(String::trim).map(String::toLong)
Program(inputC, listOf(1)).run().first().let { println(it) }
Program(inputC, listOf(2)).run().first().let { println(it) }
