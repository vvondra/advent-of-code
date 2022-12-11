import java.io.File
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.roundToLong

operator fun <T> List<T>.component6(): T = get(5)
data class Monkey(val id: Int, val items: List<BigDecimal>, val op: (BigDecimal) -> BigDecimal, val div: BigDecimal, val yes: Int, val no: Int, val inspected: Int = 0) {
    fun turn(worryDiv: BigDecimal): Pair<Monkey, Map<Int, List<BigDecimal>>> =
            items.fold(emptyMap<Int, List<BigDecimal>>()) { throws, item ->
                val worry = op(item).divide(worryDiv, RoundingMode.FLOOR)
                val target = if (worry % div == BigDecimal.ZERO) yes else no
                throws + (target to throws.getOrDefault(target, emptyList()) + worry)
            }
            .let { throws ->
                copy(items = emptyList(), inspected = inspected + throws.values.fold(0) { acc, b -> acc + b.size }) to throws
            }

    fun receive(new: List<BigDecimal>) = copy(items = items + new)
}

val monkeyInputs = File("input/11.in").readText().split("\n\n")
val divisor = monkeyInputs
    .map { input ->
        val (_, _, _, test) = input.split("\n")
        test.split(" ").last().trim().toLong()
    }
    .reduce { a, b -> a * b }
val monkeys = monkeyInputs
    .map { input ->
        val (m, s, op, test, yes, no) = input.split("\n")
        val divTest = test.split(" ").last().trim().toBigDecimal()

        Monkey(
            Regex("\\d+").find(m)!!.value.toInt(),
            s.split(":").last().trim().split(",").map { BigDecimal(it.trim()) },
            op.split("=").last().trim().let { exp ->
                val (left, operand, right) = exp.split(" ")
                fun(n: BigDecimal): BigDecimal {
                    val a = if (left == "old") n else BigDecimal(left)
                    val b = if (right == "old") n else BigDecimal(right)
                    return when (operand) {
                        "*" -> (a * b).rem(BigDecimal(divisor))
                        "+" -> a + b
                        else -> throw Exception("Unexpected $operand")
                    }
                }
            },
            divTest,
            yes.split(" ").last().trim().toInt(),
            no.split(" ").last().trim().toInt()
        )
    }

fun play(rounds: Int, worryDiv: BigDecimal) = generateSequence(monkeys.associateBy { it.id }) { round ->
    round.keys.sorted().fold(round) { turn, monkeyId ->
        val (self, thrown) = turn[monkeyId]!!.turn(worryDiv)

        turn.mapValues { if (it.key in thrown) it.value.receive(thrown[it.key]!!) else it.value } + (monkeyId to self)
    }
}
    .take(rounds + 1)
    .last()
    .let { it.values.map { it.inspected }.sortedDescending().take(2).map(Int::toLong).reduce { a, b -> a * b} }

println(play(20, BigDecimal(3)))
println(play(10000, BigDecimal.ONE)) // 28407524722
