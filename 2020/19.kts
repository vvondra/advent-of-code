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
  fun accept(regex: Node, string: String): String? {
    //println("R: ${regex.toString().padEnd(20)} S: ${string}")

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
      else -> throw Exception("What is ${regex}?")
    }
  }

  val res = accept(rules.get(0)!!, string);

  return res != null && res == success
}

inputs.count { match(regexs, it) }.let(::println)

val tests = listOf(
  "bbabbbbaabaabba",
  "babbbbaabbbbbabbbbbbaabaaabaaa",
  "aaabbbbbbaaaabaababaabababbabaaabbababababaaa",
  "bbbbbbbaaaabbbbaaabbabaaa",
  "bbbababbbbaaaaaaaabbababaaababaabab",
  "ababaaaaaabaaab",
  "ababaaaaabbbaba",
  "baabbaaaabbaaaababbaababb",
  "abbbbabbbbaaaababbbbbbaaaababb",
  "aaaaabbaabaaaaababaa",
  "aaaabbaabbaaaaaaabbbabbbaaabbaabaaa",
  "aabbbbbaabbbaaaaaabbbbbababaaaaabbaaabba",
)


fun generateLoops(): Map<Int, Node> = mapOf(
    8 to Or(List(20) { i -> Refs(List(i + 1) { 42 })}),
    11 to Or(List(20) { i -> Refs(List(i) { 42 } + List(i) { 31 })}),
  )
val override = regexs.plus(generateLoops())
println(override)
tests.forEach { if (!match(override, it)) println("${it} should match") }
inputs.count { match(override, it) }.let(::println)
