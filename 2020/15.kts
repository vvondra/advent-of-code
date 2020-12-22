data class Step(val history: MutableMap<Int, Int>, val turn: Int, val prev: Int, val beforeThat: Int? = null)

fun seq(start: List<Int>): Sequence<Step> {
  val initial = Step(
    start.withIndex().associate { it.value to it.index + 1 }.toMutableMap(),
    start.size + 1,
    start.last()
  )

  return generateSequence(initial) { (history, turn, _, beforeThat)  ->
    val spoken = if (beforeThat == null) 0 else turn - 1 - beforeThat
    val spokenBefore = history[spoken]
    history.put(spoken, turn)

    Step(history, turn + 1, spoken, spokenBefore)
  }
}

val input = listOf(19, 20, 14, 0, 9, 1)

seq(input).find { it.turn == 2021 }!!.let { println(it.prev) }
seq(input).find { it.turn == 30000001 }!!.let { println(it.prev) }
