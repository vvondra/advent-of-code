import java.io.File

data class Ins(val dir: String, val num: Int)

var instructions = File("02.input").readLines()
    .map { it.split(" ").let { Ins(it[0], it[1].toInt()) } }

instructions
    .fold(Pair(0, 0)) { (hor, ver), ins ->
        when (ins.dir) {
            "forward" -> Pair(hor + ins.num, ver)
            "down" -> Pair(hor, ver + ins.num)
            "up" -> Pair(hor, ver - ins.num)
            else -> throw Exception("Unknown instruction")
        }
    }
    .let { println(it.first * it.second) }

instructions
    .fold(Triple(0, 0, 0)) { (hor, ver, aim), ins ->
        when (ins.dir) {
            "forward" -> Triple(hor + ins.num, ver + ins.num * aim, aim)
            "down" -> Triple(hor, ver, aim + ins.num)
            "up" -> Triple(hor, ver, aim - ins.num)
            else -> throw Exception("Unknown instruction")
        }
    }
    .let { println(it.first * it.second) }
