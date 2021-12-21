import java.math.BigInteger

val startA = 3
val startB = 7

data class Game(val a: Int, val b: Int, val posA: Int, val posB: Int, val turn: Int) {
    fun play(thrown: Int): Game {
        if (turn % 2 == 0) {
            val newPos = (posA + thrown) % 10
            return copy(a = a + newPos + 1, posA = newPos, turn = turn + 1)
        } else {
            val newPos = (posB + thrown) % 10
            return copy(b = b + newPos + 1, posB = newPos, turn = turn + 1)
        }
    }
}

val init = Game(0, 0, startA - 1, startB - 1, 0)
var cache = mutableMapOf<Pair<Game, Boolean>, Long>()
val limit = 21

val ways = mutableMapOf<Int, Long>().withDefault { 0L }
for (i in 1..3) {
    for (j in 1..3) {
        for (k in 1..3) {
            ways.put(i + j + k, ways.getValue(i + j + k) + 1)
        }
    }
}

fun play(state: Game, winnerA: Boolean): Long = when {
        cache.containsKey(state to winnerA) -> cache.getValue(state to winnerA)
        state.a >= limit -> if (winnerA) 1 else 0
        state.b >= limit -> if (winnerA) 0 else 1
        else -> ways.keys
            .map { ways[it]!! * play(state.play(it), winnerA) }
            .sum()
            .also { cache.put(state to winnerA, it) }
    }

println(maxOf(play(init, true), play(init, false)))