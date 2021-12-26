import java.io.File


data class Op(val pop: Boolean, val a: Int, val b: Int)
val params = File("24.input").readLines()
    .chunked(18)

val ops = params.map {
        Op(
            it[4].endsWith('6'),
            it[5].split(' ').last().toInt(),
            it[15].split(' ').last().toInt(),
        )
    }


ops.forEach(::println)
/*
(0..17).forEach { row ->
    print("${row.toString().padEnd(2, ' ')} ")
    params.forEach {
        print(it[row].padEnd(9, ' '))
    }
    println()
}*/

fun cycle2(input: Int, zIn: Int, op: Op): Int {
    println("Input: ${input}, Op: ${op}")
    var w = input
    var z = zIn

    val head = z % 26

    z = z / if (op.pop) 26 else 1

    if (w != head + op.a) {
        z *= 26
        z += w + op.b
    }

    return z
}

fun stack(stack: Int) {
    var rem = stack
    print("[ ${rem % 26}")
    while (rem > 26) {
        rem = rem / 26
        print(" ${rem % 26} ")
    }
    println(" ]")
}

val inputs = List(14) { index ->
    if (!ops[index].pop) {
        var level = 1
        var next = index
        var instruction = ops[index]
        while (level != 0 && index < 13) {
            next = next + 1
            instruction = ops[next]
            if (instruction.pop) {
                level--
            } else {
                level++
            }
        }

        println("Matching for ${index} is ${instruction}")
        (1..9).asSequence().first { f ->
            ops[index].b + instruction.a + f in 1..9
        }
    } else 1
    /*
    part1:
    (9 downTo 1).asSequence().first { f ->
            ops[index].b + instruction.a + f in 1..9
        }
    } else 9

     */
}
var z = 0
val realInputs = mutableListOf<Int>()
ops.withIndex().forEach { (i, op) ->
    stack(z)
    if (op.pop) {
        val diff = (z % 26) + op.a
        z = cycle2(diff, z, op)
        realInputs.add(diff)
    } else {
        z = cycle2(inputs[i], z, op)
        realInputs.add(inputs[i])
    }
}

stack(z)
println(realInputs.joinToString(""))