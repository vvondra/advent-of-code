import java.io.File
import java.util.*

typealias XY = Pair<Int, Int>
typealias Dungeon = List<List<Char>>

val mapInput = File("../18.input.sample").readLines().map { it.toList() }

fun find(char: Char, map: Dungeon): XY {
  for (i in 0 until map.size) {
    for (j in 0 until map[i].size) {
      if (map[i][j] == char) {
        return i to j
      }
    }
  }
  throw Exception("Not found!")
}

fun findAll(char: Char, map: Dungeon): Sequence<XY> = sequence {
  for (i in 0 until map.size) {
    for (j in 0 until map[i].size) {
      if (map[i][j] == char) {
        yield(i to j)
      }
    }
  }
}


fun keyCount(map: Dungeon): Int = map.sumOf { it.count { it.isLowerCase() } }
val moves = listOf(1 to 0, -1 to 0, 0 to -1, 0 to 1)

data class Step(val xy: XY, val distance: Int)
data class NextStep(val xy: XY, val key: Char, val distance: Int)
fun distances(current: XY, keys: Set<Char>, map: Dungeon): Set<NextStep> {
  val history = mutableSetOf(current)

  fun neighbors(current: XY): List<XY> {
    return moves
      .map { current.first + it.first to current.second + it.second }
      .filter {
        val cell = map[it.first][it.second]
        cell != '#' &&
          !(cell.isUpperCase() && cell.toLowerCase() !in keys) &&
          it !in history
      }
  }

  val queue = neighbors(current)
    .map { Step(it, 1) }
    .toMutableList()

  val candidates = mutableSetOf<NextStep>()
  while (queue.size > 0) {
    val (xy, distance) = queue.removeFirst()
    val cell = map[xy.first][xy.second]
    //println("$distance $xy $cell")
    history.add(xy)

    if (cell.isLowerCase() && cell !in keys) {
      candidates.add(NextStep(xy, cell, distance))
      continue
    }

    neighbors(xy).forEach { queue.add(Step(it, distance + 1)) }
  }

  return candidates.toSet()
}

data class ExploreStep(val xy: List<XY>, val distance: Int, val keys: Set<Char>) : Comparable<ExploreStep> {
  override fun compareTo(other: ExploreStep): Int = distance - other.distance
}

fun explore(starts: List<XY>, map: Dungeon): Int {
  val keyCount = keyCount(map)
  val queue = PriorityQueue(listOf(ExploreStep(starts, 0, emptySet())))
  val history = mutableMapOf<Pair<List<XY>, Set<Char>>, Int>()

  while (queue.size > 0) {
    val (current, distance, keys) = queue.remove()
    //println("$distance $current $keys")

    for (robot in 0 until starts.size) {
      val nextSteps = distances(current[robot], keys, map)

      if (keys.size == keyCount) {
        return distance
      }

      nextSteps.forEach { nextStep ->
        val keyStep = keys.plus(nextStep.key)
        val robots = current.toMutableList().apply { set(robot, nextStep.xy) }.toList()
        val historyKey = robots to keyStep
        val keyDistance = distance + nextStep.distance

        if (!history.containsKey(historyKey) || keyDistance < history.get(historyKey)!!) {
          history.set(historyKey, keyDistance)
          queue.offer(ExploreStep(robots, keyDistance, keyStep))
        }
      }
    }

  }
  throw Exception("No solution")
}

val orig = find('@', mapInput);
explore(listOf(orig), mapInput).let(::println)

val split = mapInput.mapIndexed { i, row ->
  row.mapIndexed { j, char ->
    if (Math.abs(orig.first - i) == 1 && Math.abs(orig.second - j) == 1) '@'
    else if (Math.abs(orig.first - i) + Math.abs(orig.second - j) == 1) '#'
    else if (orig.first == i && orig.second == j) '#'
    else char
  }
}
explore(findAll('@', split).toList(), split).let(::println)
