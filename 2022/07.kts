val input = java.io.File("input/07.in").readLines()

sealed class Node {
    abstract val name: String
}

data class File(override val name: String, val size: Long) : Node() {
    override fun toString(): String = "${name} (file, size=${size})"
}

data class Directory(override val name: String, val children: MutableSet<Node> = mutableSetOf()) : Node() {
    override fun toString(): String = "${name} (dir)"
    fun subdirs(): Set<Directory> = children.filterIsInstance<Directory>().toSet()
}

fun print(d: Directory, indent: Int = 0) {
    println(" ".repeat(indent) + "- $d")
    d.children.forEach { child ->
        when (child) {
            is Directory -> print(child, indent + 1)
            else -> println(" ".repeat(indent + 1) + "- $child")
        }
    }
}

fun sizes(d: Directory): Map<Directory, Long> {
    val files = d.children.filterIsInstance<File>().sumOf { it.size }
    val dirs = d.children.filterIsInstance<Directory>().fold(emptyMap<Directory, Long>()) { acc, dir ->
        acc + sizes(dir)
    }

    return mapOf(d to (files + d.subdirs().sumOf { dirs[it]!! })) + dirs
}

fun traverse(inputs: List<String>): Directory {
    fun isCmd(s: String) = s.startsWith("$ ")
    fun traverse(cmds: List<String>, stack: List<Directory>): Directory {
        val cmd = cmds.firstOrNull()
        val cd = stack.last()

        return when {
            cmd != null -> when {
                cmd.startsWith("$ ls") -> {
                    traverse(cmds.drop(1), stack)
                }
                cmd.startsWith("$ cd") -> {
                    val nd = cmd.drop(5)
                    if (nd == "..") {
                        traverse(cmds.drop(1), stack.dropLast(1))
                    } else {
                        traverse(cmds.drop(1), stack + cd.children.find { it.name == nd }!! as Directory)
                    }
                }
                else -> {
                    val (type, name) = cmd.split(" ")
                    cd.children.add(
                        when {
                            type == "dir" -> Directory(name)
                            else -> File(name, type.toLong())
                        }
                    )
                    traverse(cmds.drop(1), stack)
                }
            }
            else -> stack.first()
        }
    }

    return traverse(inputs, listOf(Directory("root", mutableSetOf(Directory("/"))))).children.single() as Directory
}

val root = traverse(input)
val sizes = sizes(root)

sizes.filterValues { it <= 100000 }
    .values
    .sum()
    .let(::println)

val total = 70000000
val needed = 30000000
val used = sizes[root]!!
val toFree = needed - (total - used)

sizes.filterValues { it >= toFree }
    .minByOrNull { it.value }
    .let(::println)
