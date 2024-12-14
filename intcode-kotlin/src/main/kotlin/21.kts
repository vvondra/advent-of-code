
fun output(feed: Sequence<Long>) {
  var col = 0
  var start = 0
  var floor = false
  feed.forEach {
    when {
      it > 255 -> println(it)
      else -> {
        val char = it.toChar()
        print(char)
        when (char) {
          '\n' -> {
            if (floor) {
              print(" ".repeat(start) + "ABCDEDFGHI\n")
              start = 0
            }
            floor = false
            col = 0
          }
          '#' -> floor = true
          '@' -> start = col
        }

        col++
      }
    }
  }
}

fun encode(cmds: List<String>): List<Long> = cmds
    .joinToString("\n")
    .map { it.toLong() }

val walk = listOf(
  "NOT C J", // if third is a hole
  "AND D J", // and 4 is safe, jump
  "NOT A T",
  "OR T J",
  "WALK",
  ""
)

val run = listOf(
  "NOT B J",
  "NOT C T",
  "OR T J",
  "AND D J",
  "AND H J",
  "NOT A T",
  "OR T J",

  "RUN",
  ""
)

Program.fromFile("21.input", encode(walk)).start().let(::output)
Program.fromFile("21.input", encode(run)).start().let(::output)
