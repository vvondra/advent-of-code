import java.io.File

data class OpResult(val state: List<Long>, val ip: Int, val rp: Int, val outputs: List<Long> = emptyList())
enum class Mode {
  Position,
  Immediate,
  Relative
}

class Op(private val rp: Int, private val ip: Int, prev: List<Long>) {
  private val op: Int
  private val modes: List<Int>
  private val params: List<Long>
  init {
    val code = prev[ip]
    op = (code % 100).toInt()
    modes = (code / 100).toString(radix = 10).map { Character.getNumericValue(it) }.reversed()
    params = prev.subList(ip + 1, ip + 1 + opParamCount(op))
  }

  private fun nextStep(): Int = ip + paramCount() + 1
  private fun paramCount(): Int = opParamCount(op)
  private fun value(position: Int, state: List<Long>, writeMode: Boolean = false): Long =
    when (Mode.values()[modes.getOrElse(position) { 0 }]) {
      Mode.Position -> if (writeMode) params[position]
      else state.getOrElse(params[position].toInt()) { 0 }
      Mode.Immediate -> params[position]
      Mode.Relative -> if (writeMode) params[position] + rp
      else state.getOrElse(params[position].toInt() + rp) { 0 }
    }

  fun execute(state: List<Long>, inputFn: () -> Long): OpResult {
    val default = OpResult(state, nextStep(), rp)

    fun updated(memory: List<Long>, idx: Int, value: Long): List<Long> =
      memory.toMutableList().apply {
        // Dynamically resize memory
        val address = value(idx, this, true).toInt()
        if (address >= memory.size) {
          (memory.size..(address + 1)).forEach { add(it, 0L) }
        }
        set(address, value)
      }.toList()

    return when (op) {
      1 -> default.copy(state = updated(state, 2, value(0, state) + value(1, state)))
      2 -> default.copy(state = updated(state, 2, value(0, state) * value(1, state)))
      3 -> default.copy(state = updated(state, 0, inputFn()))
      4 -> default.copy(outputs = listOf(value(0, state)))
      5 -> if (value(0, state) != 0L) default.copy(ip = value(1, state).toInt()) else default
      6 -> if (value(0, state) == 0L) default.copy(ip = value(1, state).toInt()) else default
      7 -> default.copy(state = updated(state, 2, if (value(0, state) < value(1, state)) 1 else 0))
      8 -> default.copy(state = updated(state, 2, if (value(0, state) == value(1, state)) 1 else 0))
      9 -> default.copy(rp = default.rp + value(0, state).toInt())
      99 -> default.copy(ip = ip)
      else -> throw Exception("What is $op?")
    }
  }

  companion object {
    fun opParamCount(op: Int): Int =
      mapOf(1 to 3, 2 to 3, 3 to 1, 4 to 1, 5 to 2, 6 to 2, 7 to 3, 8 to 3, 9 to 1, 99 to 0)[op]!!
  }
}

class Program(private var state: List<Long>, inputSeed: List<Long> = emptyList()) {
  private var ip = 0
  private var rp = 0
  private var inputFn = fun(): Long = inputs.removeFirst()
  private val inputs: MutableList<Long> = inputSeed.toMutableList()

  fun valueAt(address: Int) = state[address]

  fun addInput(new: List<Long>) {
    this.inputs.addAll(new)
  }

  fun clone(): Program = Program(state, inputs).also {
    it.rp = this.rp
    it.ip = this.ip
  }

  fun start(): Sequence<Long> {
    return sequence {
      while (state[ip] != 99L) {
        Op(rp, ip, state).execute(state, inputFn).let { result ->
          ip = result.ip
          rp = result.rp
          state = result.state

          result.outputs.forEach { yield(it) }
        }
      }
    }
  }

  companion object {
    fun fromFile(file: String, inputs: List<Long> = emptyList()): Program {
      return Program(
        File(file).readText().split(",").map(String::trim).map(String::toLong),
        inputs
      )
    }
  }
}


