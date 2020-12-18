import java.io.File
import java.util.Stack

fun apply(left: Long, right: Long, op: Char): Long {
  return when (op) {
    '+' -> left + right
    '*' -> left * right
    else -> throw Exception("Unknown op: $op")
  }
}

val precedenceA = mapOf('+' to 0, '*' to 0)
val precedenceB = mapOf('+' to 1, '*' to 0)

fun evaluate(expression: String, precedence: Map<Char, Int>): Long {
  val values = Stack<Long>()
  val ops = Stack<Char>()
  expression.forEach { c ->
    when {
      c.isWhitespace() -> {}
      c.isDigit() -> {
        values.push(Character.getNumericValue(c).toLong())
      }
      c == '(' -> ops.push(c)
      c == ')' -> {
        while (ops.isNotEmpty() && ops.peek() != '(') {
          values.push(apply(values.pop(), values.pop(), ops.pop()))
        }
        if (ops.isNotEmpty()) ops.pop()
      }
      else -> {
        while (
          ops.isNotEmpty() &&
          ops.peek() != '(' &&
          precedence.get(ops.peek())!! >= precedence.get(c)!!
        ) {
          values.push(apply(values.pop(), values.pop(), ops.pop()))
        }

        ops.push(c)
      }
    }
  }

  while (ops.isNotEmpty() && ops.peek() != '(') {
    values.push(apply(values.pop(), values.pop(), ops.pop()))
  }
  if (ops.isNotEmpty()) ops.pop()

  return values.single()
}

val test = mapOf(
  "2 * 3 + (4 * 5)" to (26 to 46),
  "5 * 9 * (7 * 3 * 3 + 9 * 3 + (8 + 6 * 4))" to (12240 to 669060),
  "((2 + 4 * 9) * (6 + 9 * 8 + 6) + 6) + 2 + 4 * 2" to (13632 to 23340)
)

test.forEach { (exp, result) ->
  val (resultA, resultB) = result
  require(evaluate(exp, precedenceA) == resultA.toLong()) {
    "$exp should be equal to $result, got ${evaluate(exp, precedenceA)}"
  }

  require(evaluate(exp, precedenceB) == resultB.toLong()) {
    "$exp should be equal to $result, got ${evaluate(exp, precedenceB)}"
  }
}


File("18.input").readLines()
  .map { evaluate(it, precedenceA) to evaluate(it, precedenceB) }
  .reduce { (a, i), (b, j) -> a + b to i + j }
  .let(::println)
