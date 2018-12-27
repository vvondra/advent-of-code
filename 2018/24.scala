import scala.annotation.tailrec

val pattern = "(\\d+) units each with (\\d+) hit points\\s*[(]?([^)]*)[)]? with an attack that does (\\d+) (\\w+) damage at initiative (\\d+)".r

sealed trait Side

case object Immunity extends Side

case object Infection extends Side

case class Group(index: Int, side: Side, units: Int, hp: Int, modifiers: Map[String, Int], damage: Int, attackType: String, initiative: Int) {
  def effectivePower: Int = units * damage

  def damageDealt(to: Group): Int = effectivePower * to.modifiers.getOrElse(attackType, 1)

  def attack(target: Group): Option[Group] = {
    val defender = target.copy(units = Math.ceil(((target.units * target.hp) - damageDealt(target)).toFloat / target.hp).toInt)

    if (defender.units > 0) Some(defender) else None
  }
}

type Armies = Map[Int, Group]

val groups = io.Source.stdin.mkString
  .split("\n\n") // split the sections
  .map(_.split("\n")) // back to list of lines
  .map(_.drop(1)) // drop the army name
  .zipWithIndex
  .flatMap { case (side, i) => side.map {
    case pattern(units, hp, modifiers, damage, attackType, initiative) =>
      val m = modifiers
        .split(";")
        .map(_.trim)
        .flatMap { modifier =>
          modifier
            .stripPrefix("weak to")
            .stripPrefix("immune to ")
            .split(",")
            .map(_.trim)
            .filter(_.nonEmpty)
            .map(_ -> (if (modifier.startsWith("weak to ")) 2 else 0))
        }
        .toMap
      Group(0, if (i == 0) Immunity else Infection, units.toInt, hp.toInt, m, damage.toInt, attackType, initiative.toInt)
  }
  }
  .zipWithIndex
  .map { case (group, i) => i -> group.copy(index = i) }
  .toMap

def chooseTargets(attackers: Set[Group], defenders: Set[Group]): Map[Int, Int] = {
  attackers.toSeq
    .sortWith { (a, b) => {
      if (a.effectivePower == b.effectivePower) a.initiative > b.initiative else a.effectivePower > b.effectivePower
    }
    }
    .foldLeft((Map.empty[Int, Int], defenders)) {
      case ((assignments, targets), attacker) => if (targets.isEmpty) {
        (assignments, targets)
      } else {
        targets.toSeq
          .filter(attacker.damageDealt(_) > 0)
          .sortWith { (a, b) =>
            if (attacker.damageDealt(a) == attacker.damageDealt(b)) {
              if (a.effectivePower == b.effectivePower) {
                a.initiative > b.initiative
              } else a.effectivePower > b.effectivePower
            } else attacker.damageDealt(a) > attacker.damageDealt(b)
          }
          .headOption match {
            case Some(target) => (assignments + (attacker.index -> target.index), targets - target)
            case None => (assignments, targets)
          }
      }
    }
    ._1
}

def combatTurn(armies: Armies): Armies = {
  @tailrec
  def attack(groupToGroup: Seq[(Int, Int)], resolved: Armies): Armies = {
    if (groupToGroup.isEmpty) {
      resolved
    } else {
      val pair = groupToGroup.head
      if (resolved.contains(pair._1)) {
        resolved(pair._1).attack(resolved(pair._2)) match {
          case Some(defender) => attack(groupToGroup.tail, resolved + (defender.index -> defender))
          case None => attack(groupToGroup.tail, resolved - pair._2)
        }
      } else {
        attack(groupToGroup.tail, resolved)
      }
    }
  }

  val immunity = armies.values.filter(_.side == Immunity).toSet
  val infection = armies.values.filter(_.side == Infection).toSet
  val infectionToImmunity = chooseTargets(infection, immunity)
  val immunityToInfection = chooseTargets(immunity, infection)
  val assignments = infectionToImmunity ++ immunityToInfection
  val attackOrder = assignments.toSeq.sortBy(a => armies(a._1).initiative).reverse

  if (infectionToImmunity.isEmpty || immunityToInfection.isEmpty) {
    armies
  } else attack(attackOrder, armies)
}

@tailrec
def combat(armies: Armies): Armies = {
  if (armies.groupBy { case (_, g) => g.side }.size == 1) {
    armies
  } else {
    val afterCombat = combatTurn(armies)
    if (afterCombat == armies) {
      armies
    } else {
      combat(afterCombat)
    }
  }
}

def winningUnits(armies: Armies) = armies.values.map(_.units).sum
def winningSide(armies: Armies) = armies.values.head.side

println(groups)
val afterCombat = combat(groups)
println(winningUnits(afterCombat))
println(winningSide(afterCombat))

// Part 2
def boosted(armies: Armies, boost: Int) = armies.mapValues {
  case g: Group if g.side == Immunity => g.copy(damage = g.damage + boost)
  case g => g
}

var withBoost = groups
var boost = 1
while (winningSide(combat(withBoost)) == Infection) {
  println(boost)
  withBoost = boosted(groups, boost)
  boost = boost + 1
}

println(boost)
println(winningUnits(combat(withBoost)))