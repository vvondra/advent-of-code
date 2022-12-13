import java.io.File
import java.util.*
import kotlin.math.exp

val input = File("input/13.in")
    .readText()
    .split("\n\n")
    .map(String::trim)
    .map { pair ->
        val (first, second) = pair.split("\n")
        parse(first) to parse(second)
    }

sealed class Node {
    data class Leaf(val value: Int): Node() {
        override fun toString(): String = "$value"
    }
    data class List(val els: Collection<Node>): Node() {
        constructor(el: Node) : this(listOf(el))
        override fun toString(): String = "[${els.joinToString(",")}]"
    }
}

fun parse(expression: String): Node.List {
    var idx = 1
    fun parse(): Node.List {
        val stack = mutableListOf<Node>()
        while (true) {
            val c = expression[idx++]
            when {
                c.isDigit() -> {
                    val num = c + expression.drop(idx).takeWhile { it.isDigit() }
                    idx += num.length - 1
                    stack.add(Node.Leaf(num.toInt()))
                }
                c == '[' -> {
                    stack.add(parse())
                }
                c == ']' -> break
                c == ',' -> continue
                else -> throw Exception("Unexpected token $c")
            }
        }

        return Node.List(stack.toList())
    }

    return parse()
}

enum class Result { OK, NOK, CONTINUE }

fun compare(left: Node, right: Node): Result =
    if (left is Node.Leaf && right is Node.Leaf) compare(left, right)
    else if (left is Node.List && right is Node.Leaf) compare(left, right)
    else if (left is Node.Leaf && right is Node.List) compare(left, right)
    else if (left is Node.List && right is Node.List) compare(left, right)
    else throw Exception()

fun compare(left: Node.Leaf, right: Node.Leaf): Result = when {
    left.value < right.value -> Result.OK
    left.value == right.value -> Result.CONTINUE
    left.value > right.value -> Result.NOK
    else -> throw Exception()
}
fun compare(left: Node.Leaf, right: Node.List): Result = compare(Node.List(left), right)
fun compare(left: Node.List, right: Node.Leaf): Result = compare(left, Node.List(right))
fun compare(left: Node.List, right: Node.List): Result {
    val l = left.els.iterator()
    val r = right.els.iterator()
    while (true) {
        if (l.hasNext() && !r.hasNext()) return Result.NOK
        if (!l.hasNext() && r.hasNext()) return Result.OK
        if (!l.hasNext() && !r.hasNext()) return Result.CONTINUE

        when (compare(l.next(), r.next())) {
            Result.CONTINUE -> continue
            Result.OK -> return Result.OK
            Result.NOK -> return Result.NOK
            else -> throw Exception()
        }
    }
}

input.withIndex().filter { compare(it.value.first, it.value.second) == Result.OK }.map { it.index + 1}.sum().let(::println)

val dividerA = Node.List(Node.List(Node.Leaf(2)))
val dividerB = Node.List(Node.List(Node.Leaf(6)))

val part2 = (input.flatMap { listOf(it.first, it.second) } + listOf(dividerA, dividerB))
    .sortedWith(Comparator<Node> { a, b -> if (compare(a, b) == Result.OK) -1 else 1 })

println((part2.indexOf(dividerA) + 1) * (part2.indexOf(dividerB) + 1))
