import scala.annotation.tailrec
import scala.collection.immutable.Queue

sealed trait Race
case object Elf extends Race
case object Goblin extends Race

sealed trait Cell
case object Ground extends Cell {
  override def toString = "."
}
case object Wall extends Cell {
  override def toString = "#"
}
case class Warrior(race: Race, hp: Int = 200, attack: Int = 3) extends Cell {
  def isDead = hp <= 0

  def enemyRace = race match { case Elf => Goblin case Goblin => Elf }

  def attack(other: Warrior): Option[Warrior] = {
    val victim = other.copy(hp = other.hp - attack)
    if (victim.isDead) {
      None
    } else {
      Some(victim)
    }
  }

  override def toString = race match { case Elf => "E" case Goblin => "G" }

  def printStats(): Unit = print(s"$toString($hp)")
}

val ReadingOrder = Seq((-1, 0), (0, -1), (0, 1), (1, 0))

type Dungeon = List[List[Cell]]
implicit class Updatable2DVector[A](g: List[List[A]]) {
  def deepUpdate(x: Int, y: Int, value: A) = g.updated(x, g(x).updated(y, value))
}

case class Coord(x: Int, y: Int) extends Ordered[Coord] {
  import scala.math.Ordered.orderingToOrdered // so I can compare tuples on the line below

  override def compare(that: Coord): Int = (x, y) compare(that.x, that.y)
}

// Modified BFS which keeps track of which adjacent square was used to start
// the shortest path to a target. It doesn't care about the full path
def nextStep(c: Coord, target: Race, dungeon: Dungeon): Option[Coord] = {
  case class Path(source: Coord, next: Coord)

  def neighbours(c: Path, visited: Set[Coord]): Seq[Path] = {
    ReadingOrder
      .map { case (dx, dy) => Coord(c.next.x + dx, c.next.y + dy) }
      .filterNot(visited.contains)
      .filterNot { case Coord(x, y) => dungeon(x)(y) == Wall }
      .map(Path(c.source, _))
  }

  @tailrec
  def step(elems: Queue[Path], visited: Set[Coord]): Option[Path] = {
    if (elems.isEmpty) {
      None
    } else {
      val (head, tail) = elems.dequeue
      val newVisited = visited + head.next
      dungeon(head.next.x)(head.next.y) match {
        case Warrior(race, _, _) if race == target => Some(head)
        case Ground => step((tail ++ neighbours(head, newVisited)).distinct, newVisited)
        case Wall | _: Warrior => step(tail, newVisited)
      }
    }
  }

  val firstNeighbours = ReadingOrder
    .map { case (dx, dy) => Coord(c.x + dx, c.y + dy) }
  
  step(
    Queue(firstNeighbours
      .filterNot { case Coord(x, y) => dungeon(x)(y) == Wall }
      .map { c => Path(c, c) }
      : _*),
    firstNeighbours.toSet
  ).map(_.source)
}

def adjacentEnemy(c: Coord, target: Race, dungeon: Dungeon): Option[Coord]
  = ReadingOrder
    .map { case (dx, dy) => ((c.x + dx, c.y + dy), dungeon(c.x + dx)(c.y + dy))}
    .filter {
      case (_, cell) => cell match {
        case Warrior(race, _, _) => race == target
        case _ => false
      }
    }
    .map { case (xy, cell) => (xy, cell.asInstanceOf[Warrior]) }
    .sortBy { case ((x, y), w) => (w.hp, x, y) }
    .headOption
    .map { case (coords, _) => Coord.tupled(coords) }

def unitTurn(dungeon: Dungeon, current: Coord): Dungeon = {
  if (!dungeon(current.x)(current.y).isInstanceOf[Warrior]) {
    // The unit died in the meantime
    return dungeon
  }

  val cell = dungeon(current.x)(current.y).asInstanceOf[Warrior]

  // First find enemy and try to attack
  adjacentEnemy(current, cell.enemyRace, dungeon) match {
    case Some(enemyCoord) =>
      // Attack and end turn
      dungeon.deepUpdate(
        enemyCoord.x,
        enemyCoord.y,
        cell.attack(dungeon(enemyCoord.x)(enemyCoord.y).asInstanceOf[Warrior]) match {
          case Some(warrior) => warrior
          case None => Ground
        }
      )
    case None =>
      // No enemy is close, move
      nextStep(current, cell.enemyRace, dungeon) match {
        case Some(coord) =>
          val movedDungeon = dungeon.deepUpdate(current.x, current.y, Ground).deepUpdate(coord.x, coord.y, cell)

          // Re-check if enemy close after move
          adjacentEnemy(coord, cell.enemyRace, movedDungeon) match {
            case Some(enemyCoord) => movedDungeon.deepUpdate(
              enemyCoord.x,
              enemyCoord.y,
              cell.attack(movedDungeon(enemyCoord.x)(enemyCoord.y).asInstanceOf[Warrior]) match {
                case Some(warrior) => warrior
                case None => Ground
              }
            )
            case None => movedDungeon
        }
        case None => dungeon
      }
  }
}

def turn(dungeon: Dungeon): Dungeon = dungeon
  .zipWithIndex
  .flatMap {
    case (r, x) => r.zipWithIndex.filter {
      case (c, _) => c.isInstanceOf[Warrior]
    }.map {
      case (_, y) => Coord(x, y)
    }
  }
  .sorted
  .foldLeft(dungeon)(unitTurn)


def printTurn(dungeon: Dungeon): Unit = dungeon.foreach(r => println(r.mkString("")))

case class CombatResolution(dungeon: Dungeon, turns: Int)
def resolveCombat(dungeon: Dungeon): CombatResolution = {
  def isOver(d: Dungeon): Boolean = {
    val groups = d.flatMap(_.groupBy { case w: Warrior => w.race case _ => null} ).toMap
    !groups.contains(Elf) || !groups.contains(Goblin)
  }

  var current = dungeon
  var i = 0
  while (!isOver(current)) {
    i = i + 1
    current = turn(current)
  }

  CombatResolution(current, i)
}

def hpTotal(dungeon: Dungeon): Int = dungeon.flatMap(_.map { case w: Warrior => w.hp case _ => 0 }).sum
def scoreCombat(combatResolution: CombatResolution): Int = combatResolution.turns * hpTotal(combatResolution.dungeon)

val cells: Dungeon = io.Source.stdin.getLines
  .map(_.toList.map {
    case '#' => Wall
    case '.' => Ground
    case 'E' => Warrior(Elf)
    case 'G' => Warrior(Goblin)
  })
  .toList

println(scoreCombat(resolveCombat(cells)))

// Part Two
def resolveCombatWithFutureTech(dungeon: Dungeon, attack: Int): Option[CombatResolution] = {
  def countElves(d: Dungeon) = d.map(r => r.count(c => c.isInstanceOf[Warrior] && c.asInstanceOf[Warrior].race == Elf)).sum
  val buffedElves = dungeon.map(r => r.map {
    case w: Warrior if w.race == Elf => w.copy(attack = attack)
    case c => c
  })
  val elfCount = countElves(dungeon)
  val resolution = resolveCombat(buffedElves)

  if (countElves(resolution.dungeon) == elfCount) {
    Some(resolution)
  } else {
    None
  }
}

var attack = 4
var partTwoResolution: Option[CombatResolution] = None
do {
  partTwoResolution = resolveCombatWithFutureTech(cells, attack)
  attack = attack + 1
} while (partTwoResolution.isEmpty)

printTurn(partTwoResolution.get.dungeon)
println(attack - 1)
println(scoreCombat(partTwoResolution.get))
