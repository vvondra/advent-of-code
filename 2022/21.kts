import java.io.File
import java.util.*
import kotlin.math.max
import kotlin.math.min
import kotlin.math.ceil

val graph = File("input/21.in")
    .readLines().map { line ->
        val (result, rest) = line.split(": ")

        rest.toLongOrNull()?.let { Op.Const(result, it) } ?: rest.split(" ").let { (left, op, right) ->
            Op.Expression(result, left, right, op.single())
        }
    }
    .associateBy(Op::name)

sealed interface Op {
    fun adjacent(): Set<String>
    fun name(): String
    data class Expression(val result: String, val left: String, val right: String, val op: Char): Op {
        override fun adjacent(): Set<String> = setOf(left, right)
        override fun name(): String = result
        fun calculate(lookup: Map<String, Long>) = when(op) {
            '+' -> lookup[left]!! + lookup[right]!!
            '-' -> lookup[left]!! - lookup[right]!!
            '*' -> lookup[left]!! * lookup[right]!!
            '/' -> lookup[left]!! / lookup[right]!!
            else -> throw Exception("Unknown $op")
        }
    }
    data class Const(val result: String, val value: Long): Op {
        override fun adjacent(): Set<String> = emptySet()
        override fun name(): String = result
    }
}

fun topo(): Iterable<String> {
    val visited = mutableSetOf<String>()
    val stack = mutableListOf<String>()

    fun topoSort(node: String) {
        visited.add(node)
        graph[node]!!.adjacent().forEach { dep -> if (dep !in visited) topoSort(dep) }
        stack.add(node)
    }

    graph.keys.forEach { if (it !in visited) topoSort(it) }

    return stack
}

fun calculate(sorted: Iterable<String>) =
    sorted.fold(emptyMap<String, Long>()) { acc, opName ->
        val value = when (val op = graph[opName]!!) {
            is Op.Const -> opName to op.value
            is Op.Expression -> opName to op.calculate(acc)
        }

        acc + value
    }

println(calculate(topo()))
