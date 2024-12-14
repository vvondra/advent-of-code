import java.io.File
import java.util.*

typealias XY = Pair<Int, Int>

val map = File("../18.input.sample").readLines().map { it.toList() }

fun find(char: Char): XY {
  for (i in 0 until map.size) {
    for (j in 0 until map[i].size) {
      if (map[i][j] == char) {
        return i to j
      }
    }
  }
  throw Exception("Not found!")
}
fun keyCount(): Int = map.sumOf { it.count { it.isLowerCase() } }

data class NextStep(val xy: XY, val key: Char, val distance: Int)


data class Step(val xy: XY, val keys: Set<Char>, val distance: Int)

fun distances(): Int {
  val history = mutableSetOf(find('@') to emptySet<Char>())
  val keyCount = keyCount()

  fun neighbors(current: XY, keys: Set<Char>): Set<XY> {
    val moves = listOf(1 to 0, -1 to 0, 0 to -1, 0 to 1)

    return moves
      .map { current.first + it.first to current.second + it.second }
      .filter { map[it.first][it.second] != '#' }
      .filterNot {
        val cell = map[it.first][it.second]
        cell.isUpperCase() && cell.toLowerCase() !in keys
      }
      .filterNot { (it to keys) in history }
      .toSet()
  }

  val queue = neighbors(find('@'), emptySet())
    .map { Step(it, emptySet(), 0) }
    .toMutableList()

  while (queue.size > 0) {
    val (xy, keys, distance) = queue.removeFirst()
    if (keys.size == keyCount) {
      return distance;
    }

    val cell = map[xy.first][xy.second]
    //println("$distance $xy $cell")
    history.add(xy to keys)

    if (cell.isLowerCase() && cell !in keys) {
      val newKeys = keys.plus(cell)
      neighbors(xy, newKeys).forEach { queue.add(Step(it, newKeys,distance + 1)) }
    } else {
      neighbors(xy, keys).forEach { queue.add(Step(it, keys,distance + 1)) }
    }

  }

  throw Exception("No solution!")
}


println(distances())
