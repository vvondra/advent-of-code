import java.io.File
import java.util.*

val buffer = File("input/20.in").readLines().map { it.toLong() }.toMutableList()

class CircularList {
    inner class Node(val value: Long, var next: Node?, var prev: Node?) {
        fun offset(n: Int) = when {
            n > 0 -> forward().drop(n).first()
            else -> throw Exception("$n")
        }

        fun forward(): Sequence<Node> = generateSequence(this) { it -> it.next }

        override fun toString() = "$value"
    }

    var size = 0
    var head: Node? = null
    var tail: Node? = null
    val positions: MutableMap<Int, Node> = mutableMapOf()

    override fun toString(): String = "[${head!!.forward().take(list.size).toList().joinToString(", ")}]"

    fun groveCoordinates() = atZeroIndex(1000) + atZeroIndex(2000) + atZeroIndex(3000)
    fun atZeroIndex(n: Long): Long = positions[buffer.indexOf(0)]!!.offset((n % size).toInt()).value

    fun addLast(value: Long) {
        val t = tail
        val newNode = Node(value, head, t)
        tail = newNode
        if (t == null) {
            head = newNode
        } else {
            t.next = newNode
            head!!.prev = newNode
        }
        positions.put(size, newNode)
        size++
    }

    fun move(n: Int) {
        val node = positions[n]!!

        val offset = Math.floorMod(node.value, (size - 1)).toInt()
        if (offset == 0) return
        val target = node.offset(offset)

        val oldNodePrev = node.prev!!
        val oldNodeNext = node.next!!
        val oldTargetNext = target.next!!

        // splice it in
        target.next = node
        oldTargetNext.prev = node
        node.prev = target
        node.next = oldTargetNext

        // remove from old
        oldNodePrev.next = oldNodeNext
        oldNodeNext.prev = oldNodePrev

        if (node == head) head = oldNodeNext
    }
}

val list = CircularList()
buffer.forEach { list.addLast(it) }
buffer.withIndex().forEach { (index, _) -> list.move(index) }
println(list.groveCoordinates())

val key = 811589153
val list2 = CircularList()
buffer.forEach { list2.addLast(it * key) }
repeat(10) {
    buffer.withIndex().forEach { (index, _) -> list2.move(index) }
}
println(list2.groveCoordinates())
