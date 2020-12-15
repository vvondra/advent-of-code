val input = listOf(19,20,14,0,9,1)

data class Step(val history: Map<Int, Int>, val turn: Int, val prev: Int, val firstTime: Boolean, val beforeThat: Int? = null)

val initial = Step(
  input.withIndex().associate { it.value to it.index + 1 },
  input.size + 1,
  input.last(),
  true
)

val seq = generateSequence(initial) { (history, turn, _, firstTime, beforeThat)  ->
  if (turn % 10000 == 0) {
    println(turn)
  }
  if (firstTime) {
    Step(history.plus(0 to turn), turn + 1, 0, !history.containsKey(0), history[0])
  } else {
    val spoken = turn - 1 - beforeThat!!
    Step(history.plus(spoken to turn), turn + 1, spoken, !history.containsKey(spoken), history[spoken])
  }
}


seq.find { it.turn == 2021 }!!.let { println(it.prev) }
seq.find { it.turn == 30000000 }!!.let { println(it.prev) }
