import java.io.File

data class Ins(val on: Boolean, val x: IntRange, val y: IntRange, val z: IntRange)
data class XYZ(val x: Int, val y: Int, val z: Int)

val ins = File("22.input").readLines()
    .map { line ->
        val lineParts = line.split(" ")
        val on = lineParts[0] == "on"

        lineParts[1].split(",")
            .map { coords ->
                coords.drop(2).split("..").let { it[0].toInt()..it[1].toInt()}
            }.let { Ins(on, it[0], it[1], it[2]) }
    }

var i = 0
val res = ins.fold(mutableSetOf<XYZ>()) { acc, ins ->
    println(i++)
    if (ins.x.intersect(-50..50).isNotEmpty() &&
        ins.y.intersect(-50..50).isNotEmpty() &&
        ins.z.intersect(-50..50).isNotEmpty())
        {
            for (i in ins.x.intersect(-50..50)) {
                for (j in ins.y.intersect(-50..50)) {
                    for (k in ins.z.intersect(-50..50)) {
                        if (ins.on) {
                            acc.add(XYZ(i, j, k))
                        } else {
                            acc.remove(XYZ(i, j, k))
                        }
                    }
                }
            }
        }

    acc
}

println(res.size)

