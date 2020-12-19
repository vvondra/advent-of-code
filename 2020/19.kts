import java.io.File
import java.util.Stack

abstract class Node
data class Character(val char: Char): Node()
data class Refs(val seq: List<Int>): Node()
data class Or(val refs: List<Node>): Node()
object End: Node()

val (regexInputs, inputs) = File("19.input").readText()
  .split("\n\n")
  .let {
    it[0].split("\n") to it[1].split("\n").filterNot(String::isEmpty)
  }

val regexs = regexInputs
  .associate {
    val parts = it.split(": ")
    parts[0].toInt() to parts[1]
  }
  .mapValues { (key, value) ->
    if (value.startsWith("\"")) {
      Character(value[1])
    } else {
      value.split(" | ")
        .map { Refs(it.split(" ").map { it.toInt() }) }
        .let { if (key == 0) it.single().copy(seq = it.single().seq.plus(-1)) else Or(it) }
    }
  }
  .plus(-1 to End)

data class Context(val regex: Node, val prev: Node?, val suffix: String)

fun match(start: Node, rules: Map<Int, Node>, string: String): Boolean {
  val success = "Yeeeees!"
  fun accept(regex: Node, string: String): String? {
    return when (regex) {
      is Character -> if (string.firstOrNull() == regex.char) string.drop(1) else null
      is End -> if (string == "") success else null
      is Or -> regex.refs.map { s -> accept(s, string) }.firstOrNull { it != null }
      is Refs -> {
        regex.seq.fold(string) { acc: String?, i: Int ->
          if (acc == null) null
          else accept(rules.get(i)!!, acc)
        }
      }
      else -> throw Exception("What is ${regex}")
    }
  }

  val res = accept(start, string);

  return res != null && res == success
}

inputs.count { match(regexs.get(0)!!, regexs, it) }.let(::println)



