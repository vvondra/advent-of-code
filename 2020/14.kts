import java.io.File

interface Indexed {
  val idx: Int
  fun value(): Long = 1L.shl(idx)
}

sealed class BitSet : Indexed {
  data class One(override val idx: Int) : BitSet()
  data class Zero(override val idx: Int) : BitSet()
  data class Float(override val idx: Int) : BitSet()
}

sealed class Cmd {
  data class Mask(val ops: Set<BitSet>) : Cmd() {

    fun mask(num: Long): Long =
      ops.fold(num) { n, op ->
        when (op) {
          is BitSet.Zero -> n and op.value().inv()
          is BitSet.One -> n or op.value()
          else -> n
        }
      }

    fun floatMask(num: Long): List<Long> =
      ops.fold(listOf(num)) { nums, op ->
        when (op) {
          is BitSet.Zero -> nums
          is BitSet.One -> nums.map { n -> n or op.value() }
          is BitSet.Float -> nums.flatMap { listOf(it xor op.value(), it) }
        }
      }

    companion object {
      fun fromString(mask: String): Mask = Mask(
        mask.reversed().withIndex().map {
          when (it.value) {
            '1' -> BitSet.One(it.index)
            '0' -> BitSet.Zero(it.index)
            'X' -> BitSet.Float(it.index)
            else -> null
          }
        }.filterNotNull().toSet()
      )
    }
  }

  data class MemSet(val idx: Int, val value: Long) : Cmd()
}

val cmds = File("14.input").readLines().map {
  when (it.take(4)) {
    "mask" -> Cmd.Mask.fromString(it.drop(7))
    "mem[" -> Regex("mem\\[(\\d+)\\] = (\\d+)")
      .matchEntire(it)!!.destructured
      .let { (idx, value) -> Cmd.MemSet(idx.toInt(), value.toLong()) }
    else -> throw Exception("Unknown command")
  }
}

fun partOne(commands: Collection<Cmd>): Long =
  commands.fold(mutableMapOf<Int, Long>() to Cmd.Mask.fromString("")) { (memory, mask), cmd ->
    when (cmd) {
      is Cmd.Mask -> memory to cmd
      is Cmd.MemSet -> {
        memory.put(cmd.idx, mask.mask(cmd.value))
        memory to mask
      }
    }
  }.first.values.sum()

partOne(cmds).let(::println)

fun partTwo(commands: Collection<Cmd>): Long =
  commands.fold(mutableMapOf<Long, Long>() to Cmd.Mask.fromString("")) { (memory, mask), cmd ->
    when (cmd) {
      is Cmd.Mask -> memory to cmd
      is Cmd.MemSet -> {
        mask.floatMask(cmd.idx.toLong()).forEach { memory.put(it, cmd.value) }
        memory to mask
      }
    }
  }.first.values.sum()

partTwo(cmds).let(::println)
