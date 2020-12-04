import java.io.File

val input = File("4.input").readText()
	.split("\n\n")
	.map(String::trim)
	.map { it.replace("\n", " ") }
	.map { it.split(" ") }
	.map { it.map { pair -> pair.split(":").let { p -> p[0] to p[1] } }.toMap() }


val required = listOf("byr", "iyr", "eyr", "hgt", "hcl", "ecl", "pid")
val validators = mapOf(
	"byr" to { value: String -> value.toIntOrNull()?.let { it >= 1920 && it <= 2002 } ?: false },
	"iyr" to { value: String -> value.toIntOrNull()?.let { it >= 2010 && it <= 2020 } ?: false },
	"eyr" to { value: String -> value.toIntOrNull()?.let { it >= 2020 && it <= 2030 } ?: false },
	"hgt" to { value: String ->
		if (value.endsWith("in")) {
			value.removeSuffix("in").toIntOrNull()?.let { it >= 59 && it <= 76 } ?: false
		} else if (value.endsWith("cm")) {
			value.removeSuffix("cm").toIntOrNull()?.let { it >= 150 && it <= 193 } ?: false
		} else false
	},
	"hcl" to { value: String -> value.matches(Regex("^#[0-9a-f]{6}$")) },
	"ecl" to { value: String -> setOf("amb", "blu", "brn", "gry", "grn", "hzl", "oth").contains(value) },
	"pid" to { value: String -> value.matches(Regex("^[0-9]{9}$"))},
)

val validFirst = input.count { it.keys.containsAll(required) }
println(validFirst)

val validSecond = input
	.filter { it.keys.containsAll(required) }
	.count { it.all { (key, value) -> validators.get(key)?.invoke(value) ?: true } }

println(validSecond)
