import java.io.File

val expenses = File("1.input").readLines().map(String::toInt)

for (e1 in expenses) for (e2 in expenses) {
  if (e1 + e2 == 2020) println(e1 * e2)
}

for (e1 in expenses) for (e2 in expenses) for (e3 in expenses) {
  if (e1 + e2 + e3 == 2020) println(e1 * e2 * e3)
}
