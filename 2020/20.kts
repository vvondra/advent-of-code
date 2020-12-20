import java.io.File
import java.lang.StringBuilder
import java.util.*

val tileDefs = File("20.input").readText().split("\n\n")
val tiles = tileDefs.map(::readTile).associateBy { it.id }
val tileEdges = tiles.values.associate { it.id to it.edges() }

typealias Edge = List<Boolean>
data class Tile(val id: Int, val cells: List<Edge>) {
  fun edges(): List<Edge> {
    val set = mutableListOf(cells.first(), cells.last())

    set.addAll(
      cells
        .map { it.first() to it.last() }
        .fold(emptyList<Boolean>() to emptyList<Boolean>()) { acc, pair ->
          acc.first.plus(pair.first) to acc.second.plus(pair.second)
        }
        .let { it.toList() }
    )

    set.addAll(set.map { it.reversed() })

    return set.toList();
  }

  fun cycle(): Sequence<Tile> {
    fun rotate(list: List<Edge>): List<Edge> {
      val size = list.size
      var dest = MutableList(size) { MutableList(size) { false } }
      for (i in 0 until size) {
        for (j in 0 until size) {
          dest[i][j] = list.get(size - 1 - j).get(i)
        }
      }

      return dest.toList().map { it.toList() }
    }

    return sequence {
      var last = this@Tile
      repeat(4) {
        last = last.copy(cells = rotate(last.cells))
        yield(last)
      }
      last = last.copy(cells = last.cells.map { it.reversed() })
      repeat(4) {
        last = last.copy(cells = rotate(last.cells))
        yield(last)
      }
    }
  }

  fun top(): Edge = cells.first()
  fun left(): Edge = cells.map { it.first() }
  fun right(): Edge = cells.map { it.last() }
  fun bottom(): Edge = cells.last()

  fun stripBorder(): Tile {
    return this.copy(cells = cells.drop(1).dropLast(1).map { it.drop(1).dropLast(1) })
  }

  override fun toString(): String {
    val sb = StringBuilder()
    sb.appendLine("Tile ${id}:")
    for (i in 0 until cells.size) {
      for (j in 0 until cells.size) {
        sb.append(if(cells.get(i).get(j)) "#" else ".")
      }
      sb.appendLine()
    }

    return sb.toString()
  }
}

fun readTile(s: String): Tile {
  val split = s.split("\n").map(String::trim)

  return Tile(
    split.first().drop(5).dropLast(1).toInt(),
    split.drop(1).filter(String::isNotEmpty).map { it.toList().map { it == '#' } }
  )
}

fun findNext(blacklist: List<Int>, top: Edge?, left: Edge?): Tile {
  return tiles.values
    .flatMap { it.cycle() }
    .find {
      it.id !in blacklist && (left == null || it.left() == left) && (top == null || it.top() == top)
    }!!
}


// Put all possible edges in a map and count for each tile how many matching edges it has
// The tile which have two edges which don't match anything else are the corners

val edgeCounts = tileEdges.values.flatten().groupingBy { it }.eachCount()
val tileEdgePairingCounts = tileEdges.mapValues { (_, h) -> h.map { edgeCounts.get(it)!! }.groupingBy { it }.eachCount() }
val max = tileEdgePairingCounts.maxOfOrNull { (_, v) -> v.getOrDefault(1, 0) }
val corners = tileEdgePairingCounts.filterValues { it.getOrDefault(1, 0) == max }

println(corners.keys.map(Int::toLong).reduce(Long::times))

val init = tiles.get(corners.keys.first())!!; // this will be our top left corner
val corner = init.cycle().find {
  edgeCounts[it.top()]!! == 1 && edgeCounts[it.left()]!! == 1
}!!

val side = tiles.size.let { Math.sqrt(it + 0.0).toInt() }
var final = MutableList(side) { MutableList<Tile?>(side) { null } }
final[0][0] = corner
var blacklist = mutableListOf(corner.id)

for (i in 0 until side) {
  for (j in 0 until side) {
    if (i == 0 && j == 0) continue
    val next = findNext(
      blacklist,
      final.getOrNull(i - 1)?.getOrNull(j)?.bottom(),
      final.getOrNull(i)?.getOrNull(j - 1)?.right(),
    )
    blacklist.add(next.id)
    final[i][j] = next
  }
}

for (i in 0 until side) {
  for (j in 0 until side) {
    final[i][j] = final[i][j]!!.stripBorder()
  }
}

val tileSize = final[0][0]!!.cells.size
val stiched = Tile(0, List(side * tileSize) { i ->
  List (side * tileSize) { j ->
    final[i / tileSize][j / tileSize]!!.cells[i % tileSize][j % tileSize]
  }
})

val monster = listOf(
  "                  # ",
  "#    ##    ##    ###",
  " #  #  #  #  #  #   "
)
val padding = (side * tileSize) - monster.first().length
val monsterRegex = monster.map { r -> "(" + r.replace(" ", "[#.]") + ")" }
  .joinToString("[#.]".repeat(padding))
  .let { Regex(it) }
val monsterChars = monster.joinToString("").count { it == '#' }

val result = stiched.cycle().map {
  println("====")
  val line = it.cells.flatten().map { if (it) '#' else '.' }.joinToString("")
  val matches = monsterRegex.findAll(line)
  matches.forEach { println(it.groupValues) }
  val matchCount = matches.count()
  if (matchCount > 0) {
    line.count { it == '#' } - (matchCount * monsterChars)
  } else 0
}.sum()

println(result)

