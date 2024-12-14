import java.io.File

val snafu = File("input/25.in").readLines()

fun toDecimal(snafu: String): Long = snafu.reversed().withIndex()
    .fold(0L) { acc, (i, c) ->
        acc + (Math.pow(5.0, i.toDouble()) * when (c) {
            '2' -> 2
            '1' -> 1
            '0' -> 0
            '-' -> -1
            '=' -> -2
            else -> throw Exception("$c")
        }).toLong()
    }

val bob = snafu.map(::toDecimal).sum()

println(bob)

bob.toString().fold("" to 0) { (acc, carry), next ->

}
