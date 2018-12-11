val numPlayers = 458
val highestMarble = 7201900

case class Turn(currentPlayer: Int, game: Seq[Int], scores: Map[Int, Long])

def shiftLeft[T](sequence: Seq[T], shift: Int): Seq[T] =
  if (shift < 0) shiftRight(sequence, Math.abs(shift))
  else shiftRight(sequence, sequence.size - shift)

def shiftRight[T](sequence: Seq[T], shift: Int): Seq[T] =
  if (shift == 0 || sequence.isEmpty) {
    sequence
  }  else {
    val split = (sequence.size - shift) % sequence.size
    sequence.drop(split) ++ sequence.take(split)
  }


def printGame(turn: Turn): Unit = {
  println(turn.game.mkString(", "))
}

val lastTurn = (0 to highestMarble).foldLeft(Turn(0, Vector.empty, Map().withDefaultValue(0))) {
  case (Turn(currentPlayer, game, scores), marble) =>
    val (newGame, newScores) = if (marble % 23 == 0 && marble > 0) {
      val backSeven = shiftRight(game, 7)
      val newScores = scores + (currentPlayer -> (scores(currentPlayer) + marble + backSeven.head))

      (backSeven.drop(1), newScores)
    } else {
      val shifted = shiftLeft(game, 2)
      (marble +: shifted, scores)
    }

    Turn((currentPlayer + 1) % numPlayers, newGame, newScores)
}

println(lastTurn.scores.values.max)
