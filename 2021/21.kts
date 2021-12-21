val startA = 3
val startB = 7

fun die() = sequence {
    var i = 1
    while (true) {
        yield(i)
        i += 1
        if (i == 101) {
            i = 1
        }
    }
}.chunked(3)

val game: Sequence<Triple<Int, Int, Int>> = sequence {
    var turn = 0
    var a = 0
    var b = 0
    var posA = startA - 1
    var posB = startB - 1
    var die = die()

    while (true) {
        val throws = die.take(2).toList()

        die = die.drop(2)

        posA = (posA + throws.first().sum()) % 10
        a += posA + 1
        turn += 3
        yield(Triple(a, b, turn))


        posB = (posB + throws.last().sum()) % 10
        b += posB + 1
        turn += 3
        yield(Triple(a, b, turn))
    }
}

val last = game.dropWhile { (a, b) -> a < 1000 && b < 1000}.first()

println(last)
println(minOf(last.first, last.second) * last.third)