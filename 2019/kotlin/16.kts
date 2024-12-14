
fun pattern(position: Int) = sequence {
    val base = listOf(0, 1, 0, -1)
    var digit = 0;

    while (true) {
      repeat(position) {
        yield(base[digit])
      }
      digit = (digit + 1) % base.size
    }
  }
  .drop(1)

fun phase(digits: Sequence<Int>): Sequence<Int> =
  digits.mapIndexed { index, _ ->
    digits
      .zip(pattern(index + 1)) { a, b -> a * b }
      .sum()
      .let { Math.abs(it) % 10 }
  }.toList().asSequence()

fun phase2(digits: List<Int>): List<Int> =
  digits
    .reversed()
    .runningReduce { acc: Int, i: Int -> acc + i }
    .map { Math.abs(it) % 10 }
    .reversed()

fun multiply(digits: List<Int>, times: Int): Sequence<Int> = sequence {
  repeat(times) {
    yieldAll(digits)
  }
}

val input = "59765216634952147735419588186168416807782379738264316903583191841332176615408501571822799985693486107923593120590306960233536388988005024546603711148197317530759761108192873368036493650979511670847453153419517502952341650871529652340965572616173116797325184487863348469473923502602634441664981644497228824291038379070674902022830063886132391030654984448597653164862228739130676400263409084489497532639289817792086185750575438406913771907404006452592544814929272796192646846314361074786728172308710864379023028807580948201199540396460310280533771566824603456581043215999473424395046570134221182852363891114374810263887875638355730605895695123598637121".map { Character.getNumericValue(it) }

(1..100)
  .fold(input.asSequence()) { acc, _ -> phase(acc) }
  .take(8)
  .joinToString("").let(::println)

val midpoint = input.size * 10000 / 2
val offset = input.take(7).joinToString("").toInt()

(1..100)
  .fold(multiply(input, 10000).drop(midpoint).toList()) { acc, _ -> phase2(acc) }
  .drop(offset - midpoint)
  .take(8)
  .joinToString("").let(::println)
