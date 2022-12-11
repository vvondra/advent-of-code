import java.io.File
import java.util.*
import kotlin.math.abs

val cmds = File("input/10.in")
    .readLines()
    .map { it.split(" ") }
    .map { when(it[0]) {
        "noop" -> Op.Noop
        "addx" -> Op.Addx(it[1].toInt())
        else -> throw Exception("Unknown op ${it[0]}")
    } }

sealed class Op {
    abstract val cycles: Int
    abstract fun exec(registers: Map<Char, Int>): Map<Char, Int>

    object Noop: Op() {
        override val cycles: Int = 1
        override fun exec(registers: Map<Char, Int>): Map<Char, Int> = registers
    }

    class Addx(val n: Int): Op() {
        override val cycles: Int = 2
        override fun exec(registers: Map<Char, Int>): Map<Char, Int> = registers + ('X' to registers['X']!! + n)
    }
}

data class State(val pc: Int, val cycle: Int, val reg: Map<Char, Int>, val history: Map<Int, Int> = emptyMap())

val breakpoints = (0..5).map { 20 + 40 * it }

val state = generateSequence(State(0,1, mapOf('X' to 1), emptyMap())) { (pc, cycle, reg, history) ->
    if (pc >= cmds.size) {
        null
    } else {
        val cmd = cmds[pc]

        val nextCycle = cycle + cmd.cycles
        val nextValue = cmd.exec(reg)
        val breakpoint = breakpoints.firstOrNull { it < cycle + cmd.cycles && !history.containsKey(it) }

        State(pc + 1, nextCycle, nextValue, if (breakpoint != null) history + (breakpoint to reg['X']!!) else history)
    }
}.last()

println(state.history.entries.fold(0) { acc, entry -> acc + entry.value * entry.key })

generateSequence(State(0,1, mapOf('X' to 1), emptyMap())) { (pc, cycle, reg) ->
    if (pc >= cmds.size) {
        null
    } else {
        val cmd = cmds[pc]

        val nextCycle = cycle + cmd.cycles
        val nextValue = cmd.exec(reg)

        (cycle until nextCycle).forEach { c ->
            if (c % 40 in reg['X']!!..reg['X']!! + 2) print("█") else print(".")
            if (c % 40 == 0) println()
        }

        State(pc + 1, nextCycle, nextValue)
    }
}.last()
