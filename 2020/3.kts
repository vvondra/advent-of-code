import java.io.File

data class Step(val right: Int, val down: Int)

val input = File("3.input").readLines().map { it.trim().split("").filter { it.isNotEmpty() } }

fun treesMet(hill: List<List<String>>, step: Step): Long {
  var row = 0
  var col = 0
  var trees: Long = 0
  while (row < hill.count()) {
    if (hill[row][col] == "#") {
      trees = trees + 1
    }

    row += step.down
    col = (col + step.right) % hill[0].count()
  }

  return trees
}

val scenarios = listOf(
  Step(3, 1),
  Step(1, 1),
  Step(5, 1),
  Step(7, 1),
  Step(1, 2)
)

val trees = scenarios.map { treesMet(input, it) }

println(trees.first())
println(trees.reduce(Long::times))
