import java.io.File

fun combat(playerA: List<Int>, playerB: List<Int>): List<Int> {
  val deckA = ArrayDeque(playerA)
  val deckB = ArrayDeque(playerB)

  while (deckA.isNotEmpty() && deckB.isNotEmpty()) {
    val cardA = deckA.removeFirst()
    val cardB = deckB.removeFirst()

    if (cardA > cardB) {
      deckA.addLast(cardA)
      deckA.addLast(cardB)
    } else {
      deckB.addLast(cardB)
      deckB.addLast(cardA)
    }
  }

  return if (deckA.isEmpty()) deckB else deckA
}

data class Turn(val aWins: Boolean, val deckA: List<Int>, val deckB: List<Int>)

fun recursiveCombat(playerA: List<Int>, playerB: List<Int>): List<Int> {
  val states = mutableSetOf<Pair<List<Int>, List<Int>>>()

  fun subgame(subplayerA: List<Int>, subplayerB: List<Int>): Turn {
    val deckA = ArrayDeque(subplayerA)
    val deckB = ArrayDeque(subplayerB)

    while (deckA.isNotEmpty() && deckB.isNotEmpty()) {
      if (states.contains(deckA to deckB)) {
        return Turn(true, deckA, deckB);
      }
      states.add(deckA to deckB)

      val cardA = deckA.removeFirst()
      val cardB = deckB.removeFirst()

      val aWins = if (deckA.size >= cardA && deckB.size >= cardB) {
        subgame(
          deckA.take(cardA),
          deckB.take(cardB)
        ).aWins
      } else {
        cardA > cardB
      }

      if (aWins) {
        deckA.addLast(cardA)
        deckA.addLast(cardB)
      } else {
        deckB.addLast(cardB)
        deckB.addLast(cardA)
      }
    }

    return Turn(deckA.isNotEmpty(), deckA, deckB)
  }

  val (aWins, resultA, resultB) = subgame(playerA, playerB)

  return if (aWins) resultA else resultB
}

fun score(deck: List<Int>): Int = deck.reversed().mapIndexed { i, card -> (i + 1) * card }.sum()

val decks = File("22.input").readText().split("\n\n")
  .map { it.trim().split("\n").drop(1).map(String::toInt) }

combat(decks[0], decks[1]).let { println(score(it)) }
recursiveCombat(decks[0], decks[1]).let { println(score(it)) }
