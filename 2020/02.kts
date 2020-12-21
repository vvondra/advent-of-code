import java.io.File

data class Password(val low: Int, val high: Int, val letter: Char, val password: String)

val expenses = File("2.input").readLines()
  .map { Regex("(\\d+)-(\\d+) ([a-z]): (\\w+)").find(it)!!.destructured }
  .map { (low, high, letter, password) -> Password(low.toInt(), high.toInt(), letter.single(), password) }

expenses
  .filter { p -> p.password.count { it == p.letter } in p.low..p.high }
  .count()
  .let(::println)

expenses
  .filter { p -> (p.password[p.low - 1] == p.letter) xor (p.password[p.high - 1] == p.letter) }
  .count()
  .let(::println)
