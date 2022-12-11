import java.io.File

operator fun <T> List<T>.component6(): T = get(5)
data class Monkey(val id: Int, val items: List<Long>, val op: (Long) -> Long, val div: Long, val yes: Int, val no: Int, val inspected: Long = 0) {
    fun turn(postOp: (Long) -> Long): Pair<Monkey, Map<Int, List<Long>>> =
            items.fold(emptyMap<Int, List<Long>>()) { throws, item ->
                val worry = postOp(op(item))
                val target = if (worry % div == 0L) yes else no
                throws + (target to throws.getOrDefault(target, emptyList()) + worry)
            }
            .let { throws ->
                copy(items = emptyList(), inspected = inspected + throws.values.fold(0) { acc, b -> acc + b.size }) to throws
            }

    fun receive(new: List<Long>) = copy(items = items + new)
}

val monkeys = File("input/11.in")
    .readText()
    .split("\n\n")
    .map { input ->
        val (m, s, op, test, yes, no) = input.split("\n")
        val divTest = test.split(" ").last().trim().toLong()

        Monkey(
            Regex("\\d+").find(m)!!.value.toInt(),
            s.split(":").last().trim().split(",").map { it.trim().toLong() },
            op.split("=").last().trim().let { exp ->
                val (left, operand, right) = exp.split(" ")
                fun(n: Long): Long {
                    val a = if (left == "old") n else left.toLong()
                    val b = if (right == "old") n else right.toLong()
                    return when (operand) {
                        "*" -> a * b
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

fun play(rounds: Int, postOp: (Long) -> Long) = generateSequence(monkeys.associateBy { it.id }) { round ->
    round.keys.sorted().fold(round) { turn, monkeyId ->
        val (self, thrown) = turn[monkeyId]!!.turn(postOp)

        turn.mapValues { it.value.receive(thrown.getOrDefault(it.key, emptyList())) } + (monkeyId to self)
    }
}
    .take(rounds + 1)
    .last()
    .let { it.values.map { it.inspected }.sortedDescending().take(2).reduce(Long::times) }

println(play(20) { it -> it / 3L })
println(play(10000, { it -> it.rem(monkeys.map { it.div }.reduce(Long::times))}))
