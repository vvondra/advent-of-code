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
        fun other(name: String) = if (left == name) right else left
    }
    data class Const(val result: String, val value: Long): Op {
        override fun adjacent(): Set<String> = emptySet()
        override fun name(): String = result
    }
}

fun topoSort(): Iterable<String> {
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

fun calculate(sorted: Iterable<String>, node: String) =
    sorted.fold(emptyMap<String, Long>()) { acc, opName ->
        val value = when (val op = graph[opName]!!) {
            is Op.Const -> opName to op.value
            is Op.Expression -> opName to op.calculate(acc)
        }

        acc + value
    }[node]!!

val sorted = topoSort()
println(calculate(sorted, "root"))

fun findPathToHuman(start: String): List<String>? {
    if (start == "humn") return listOf(start)
    val adjacent = graph[start]!!.adjacent()
    if (adjacent.isEmpty()) return null

    return adjacent.firstNotNullOfOrNull { next ->
        val path = findPathToHuman(next)
        if (path == null) null else listOf(start) + path
    }
}

val rootToHuman = findPathToHuman("root")!!

fun descend(path: List<String>, match: Long?): Long {
    if (path == listOf("humn")) return match!!

    val top = (graph[path.first()]!! as Op.Expression)
    val other = top.other(path[1])
    val constant = calculate(sorted, other)

    val op = if (top.name() == "root") '=' else top.op
    //println("${path.first()} has to equal ${match}, constant subtree is ${other}:${constant}, op is ${op}")

    return when (op) {
        '+' -> descend(path.drop(1),   match!! - constant)
        '*' -> descend(path.drop(1), match!! / constant)
        '-' -> {
            if (other == top.right) descend(path.drop(1),  constant + match!!)
            else descend(path.drop(1),  constant - match!!)
        }
        '/' -> {
            if (other == top.right) descend(path.drop(1),  constant * match!!)
            else descend(path.drop(1),  constant / match!!)
        }
        '=' -> descend(path.drop(1), constant)
        else -> throw Exception("Unknown ${op}")
    }
}

println(descend(rootToHuman, null))