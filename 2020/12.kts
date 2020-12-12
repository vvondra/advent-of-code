import java.io.File

interface Nav {
  fun advance(cmd: Pair<Char, Int>): Nav
  fun distance(): Int
}

data class ShipNav(val azi: Int, val x: Int, val y: Int) : Nav {
  val azimuths = mapOf(0 to 'N', 90 to 'E', 180 to 'S', 270 to 'W')

  override fun advance(cmd: Pair<Char, Int>): ShipNav {
    val (dir, move) = cmd;

    return when (dir) {
      'N' -> this.copy(x = x - move)
      'S' -> this.copy(x = x + move)
      'E' -> this.copy(y = y - move)
      'W' -> this.copy(y = y + move)
      'L' -> this.copy(azi = Math.floorMod(azi - move, 360))
      'R' -> this.copy(azi = Math.floorMod(azi + move, 360))
      'F' -> advance(azimuths[azi]!! to move)
      else -> throw Exception("Off course")
    }
  }

  override fun distance(): Int = Math.abs(y) + Math.abs(x)
}

data class WaypointNav(val wx: Int, val wy: Int, val sx: Int, val sy: Int): Nav {
  override fun advance(cmd: Pair<Char, Int>): WaypointNav {
    val (dir, move) = cmd;

    return when (dir) {
      'N' -> this.copy(wx = wx - move)
      'S' -> this.copy(wx = wx + move)
      'E' -> this.copy(wy = wy + move)
      'W' -> this.copy(wy = wy - move)
      'L' -> advance('R' to 360 - move)
      'R' -> if (move == 0) this else this.copy(wx = wy, wy = -wx).advance('R' to move - 90)
      'F' -> this.copy(sx = sx + wx * move, sy = sy + wy * move)
      else -> throw Exception("Off course")
    }
  }

  override fun distance(): Int = Math.abs(sx) + Math.abs(sy)
}

val commands = File("12.input").readLines().map { Pair(it.first(), it.drop(1).toInt()) }

fun solution(nav: Nav): Int = commands.fold(nav, Nav::advance).distance()

listOf(ShipNav(90, 0, 0), WaypointNav(sx = 0, sy = 0, wx = -1, wy = 10))
  .forEach { solution(it).let(::println) }
