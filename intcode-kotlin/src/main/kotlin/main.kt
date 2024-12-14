import java.io.File

fun main(args: Array<String>) {
  println("===2===")

  val inputA = File("2.input").readText().split(",").map(String::trim).map(String::toLong).toMutableList().apply {
    set(1, 12)
    set(2, 2)
  }.toList()

  Program(inputA).run {
    start().count()
    valueAt(0)
  }.let { println(it) }

  println("===5===")
  Program.fromFile("5.input", listOf(1)).start().filterNot { it > 0L }.forEach { println(it) }
  Program.fromFile("5.input", listOf(5)).start().last().let { println(it) }

  println("===9===")
  Program.fromFile("9.input", listOf(1)).start().first().let { println(it) }
  Program.fromFile("9.input", listOf(2)).start().first().let { println(it) }
}