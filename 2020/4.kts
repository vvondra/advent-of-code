import java.io.File

val input = File("4.input").readText()
	.split("\n\n")
	.map { it.trim() }
	.map { it.replace("\n", " ") }
	.map { it.split(" ") }
	.map { it.map { pair -> pair.split(":").let { p -> p[0] to p[1] } }.toMap() }


val validators: Map<String, (String) -> Boolean> = mapOf(
	"byr" to { value -> value.toIntOrNull()?.let { it >= 1920 && it <= 2002 } ?: false },
	"iyr" to { value -> value.toIntOrNull()?.let { it >= 2010 && it <= 2020 } ?: false },
	"eyr" to { value -> value.toIntOrNull()?.let { it >= 2020 && it <= 2030 } ?: false },
	"hgt" to { value ->
		if (value.endsWith("in")) {
			value.removeSuffix("in").toIntOrNull()?.let { it >= 59 && it <= 76 } ?: false
		} else if (value.endsWith("cm")) {
			value.removeSuffix("cm").toIntOrNull()?.let { it >= 150 && it <= 193 } ?: false
		} else false
	},
	"hcl" to { value -> value.matches(Regex("^#[0-9a-f]{6}$")) },
	"ecl" to { value -> setOf("amb", "blu", "brn", "gry", "grn", "hzl", "oth").contains(value) },
	"pid" to { value -> value.matches(Regex("^[0-9]{9}$"))},
)

val validFirst = input.count { it.keys.containsAll(validators.keys) }
println(validFirst)

val validSecond = input
	.filter { it.keys.containsAll(validators.keys) }
	.count { it.all { (key, value) -> validators.get(key)?.invoke(value) ?: true } }

println(validSecond)
