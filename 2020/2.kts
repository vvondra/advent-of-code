import java.io.File

data class Password(val low: Int, val high: Int, val letter: Char, val password: String)

val expenses = File("2.input").readLines()
	.map {
		val match = Regex("(\\d+)-(\\d+) ([a-z]): (\\w+)").find(it)!!
		match.destructured.let { (low, high, letter, password) -> Password(low.toInt(), high.toInt(), letter.single(), password) }
	}

val partOne = expenses
	.filter { p -> p.password.count { it == p.letter } in p.low..p.high }
	.count()

val partTwo = expenses
	.filter { p -> (p.password[p.low - 1] == p.letter) xor (p.password[p.high - 1] == p.letter)}
	.count()

println(partOne)
println(partTwo)
