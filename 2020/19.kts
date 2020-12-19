import java.io.File

abstract class Rule
data class Character(val char: Char) : Rule()
data class Refs(val seq: List<Int>) : Rule()
data class Or(val refs: List<Rule>) : Rule()
object End : Rule()

val (regexInputs, inputs) = File("19.input").readText()
  .split("\n\n")
  .let { it[0].split("\n") to it[1].split("\n").filterNot(String::isEmpty) }

fun parseRules(r: List<String>): Map<Int, Rule> =
  r.associate { it.split(": ").let { it[0].toInt() to it[1] } }
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

fun match(rules: Map<Int, Rule>, string: String): Boolean {
  val success = "Yeeeees!"
  fun accept(regex: Rule, string: List<String>): List<String> {
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
      else -> throw Exception("What is $regex?")
    }
  }

  val res = accept(rules.get(0)!!, listOf(string))

  return res.contains(success)
}

val regexs = parseRules(regexInputs)

inputs.count { match(regexs, it) }.let(::println)

val override = regexs.plus(parseRules(listOf("8: 42 | 42 8", "11: 42 31 | 42 11 31")))
inputs.count { match(override, it) }.let(::println)
