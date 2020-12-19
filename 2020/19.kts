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

fun parseRules(r: List<String> ) =
  r.associate {
    val parts = it.split(": ")
    parts[0].toInt() to parts[1]
  }
  .mapValues { (key, value) ->
    if (value.startsWith("\"")) {
      Character(value[1])
    } else {
      value.split(" | ")
        .map { Refs(it.split(" ").map { it.toInt() }) }
        .let { if (key == 0) it.single().copy(seq = it.single().seq.plus(-1)) else Or(it.reversed()) }
    }
  }
  .plus(-1 to End)

val regexs = parseRules(regexInputs)

data class Context(val regex: Node, val prev: Node?, val suffix: String)

fun match(rules: Map<Int, Node>, string: String): Boolean {
  val success = "Yeeeees!"
  fun accept(regex: Node, string: List<String>): List<String> {
    return when (regex) {
      is Character -> string.filter { it.firstOrNull() == regex.char }.map { it.drop(1) }
      is End -> if (string.contains("")) listOf(success) else emptyList()
      is Or -> regex.refs.map { s -> accept(s, string) }.flatten().filterNotNull().distinct()
      is Refs -> {
        regex.seq.fold(string) { acc: List<String>, i: Int ->
          if (acc.isEmpty()) acc
          else accept(rules.get(i)!!, acc)
        }
      }
      else -> throw Exception("What is ${regex}?")
    }
  }

  val res = accept(rules.get(0)!!, listOf(string));

  return res.contains(success)
}

inputs.count { match(regexs, it) }.let(::println)

val partTwo = mapOf(
    8 to Or(List(30) { i -> Refs(List(i + 1) { 42 })}),
    11 to Or(List(30) { i -> Refs(List(i + 1) { 42 } + List(i + 1) { 31 })}),
  )
val override = regexs.plus(partTwo)
inputs.count { match(override, it) }.let(::println)
