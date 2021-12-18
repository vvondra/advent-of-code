import java.io.File
import java.util.Stack

sealed interface Num
data class Value(var n: Int) : Num {
    override fun toString(): String {
        return "${n}"
    }
}

data class Pair(var a: Num, var b: Num, var parent: Pair? = null) : Num {
    override fun toString(): String {
        return "[${a},${b}]"
    }

    fun magnitude(): Int {
        val ma = when (a) {
            is Pair -> (a as Pair).magnitude()
            is Value -> (a as Value).n
            else -> throw Exception("nono")
        }

        val mb = when (b) {
            is Pair -> (b as Pair).magnitude()
            is Value -> (b as Value).n
            else -> throw Exception("nono")
        }

        return 3 * ma + 2 * mb
    }

    fun ordinary(): Boolean = a is Value && b is Value
}

fun parse(num: String): Pair {
    val ops = Stack<Char>()
    val vals = Stack<Num>()

    num.forEach { c ->
        when {
            c.isDigit() -> vals.push(Value(c.digitToInt()))
            c == '[' -> ops.push(c)
            c == ']' -> {
                while (ops.isNotEmpty() && ops.peek() != '[') {
                    val right = vals.pop()
                    vals.push(Pair(vals.pop(), right))
                    ops.pop()
                }
                ops.pop()
            }
            c == ',' -> ops.push(c)
        }
    }

    while (ops.isNotEmpty() && ops.peek() != '[') {
        val right = vals.pop()
        vals.push(Pair(vals.pop(), right))
    }

    fun parent(num: Pair, parent: Pair?): Pair {
        num.parent = parent
        if (num.a is Pair) parent(num.a as Pair, num)
        if (num.b is Pair) parent(num.b as Pair, num)

        return num
    }

    return parent(vals.single() as Pair, null)
}

fun explode(num: Pair): Boolean {
    fun nested(num: Pair, depth: Int): Pair? = when {
        depth == 4 && num.ordinary() -> num
        num.a is Pair -> nested(num.a as Pair, depth + 1) ?: if (num.b is Pair) nested(
            num.b as Pair,
            depth + 1
        ) else null
        num.b is Pair -> nested(num.b as Pair, depth + 1)
        else -> null
    }

    fun fixLeft(start: Pair, value: Value) {
        var next: Pair? = start
        while (next?.parent?.a === next) {
            next = next?.parent
        }
        if (next?.parent == null) {
            return
        }
        var down: Num = (next.parent as Pair).a
        while (down is Pair) {
            down = down.b
        }

        val right = down as Value;
        right.n = right.n + value.n
    }

    fun fixRight(start: Pair, value: Value) {
        var next: Pair? = start
        while (next?.parent?.b === next) {
            next = next?.parent
        }
        if (next?.parent == null) {
            return
        }
        var down: Num = (next?.parent as Pair).b
        while (down is Pair) {
            down = (down as Pair).a
        }

        val right = down as Value;
        right.n = right.n + value.n
    }

    return nested(num, 0)
        ?.let { exp ->
            val parent = exp.parent!!

            fixLeft(exp, exp.a as Value)
            fixRight(exp, exp.b as Value)

            if (parent.a === exp) {
                parent.a = Value(0)
            }
            if (parent.b === exp) {
                parent.b = Value(0)
            }

            true
        } ?: false
}

fun maxDepth(num: Num, depth: Int = 0): Int {
    if (num is Value) {
        return depth;
    } else {
        val pair = num as Pair
        return maxOf(maxDepth(pair.a, depth + 1), maxDepth(pair.b, depth + 1))
    }
}

fun split(num: Pair): Boolean {
    if (maxDepth(num) > 4) {
        throw Exception("depth invariant broken")
    }
    if (num.a is Value && (num.a as Value).n >= 10) {
        num.a = Pair(
            Value((num.a as Value).n / 2),
            Value(Math.ceil((num.a as Value).n / 2.0).toInt()),
            num
        )
        return true
    }

    if (num.a is Pair) {
        if (split(num.a as Pair)) {
            return true
        }
    }

    if (num.b is Pair) {
        return split(num.b as Pair)
    }

    if (num.b is Value && (num.b as Value).n >= 10) {
        num.b = Pair(
            Value((num.b as Value).n / 2),
            Value(Math.ceil((num.b as Value).n / 2.0).toInt()),
            num
        )
        return true
    }

    return false
}

fun reduce(num: Pair): Unit {
    if (explode(num) || split(num)) reduce(num)
}

fun sumPairs(a: Pair, b: Pair): Pair {
    val new = Pair(a, b)

    a.parent = new
    b.parent = new

    reduce(new)

    return new
}

var explosions = mapOf(
    "[[[[[9,8],1],2],3],4]" to "[[[[0,9],2],3],4]",
    "[7,[6,[5,[4,[3,2]]]]]" to "[7,[6,[5,[7,0]]]]",
    "[[6,[5,[4,[3,2]]]],1]" to "[[6,[5,[7,0]]],3]",
    "[[3,[2,[1,[7,3]]]],[6,[5,[4,[3,2]]]]]" to "[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]",
    "[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]" to "[[3,[2,[8,0]]],[9,[5,[7,0]]]]",
    "[[[[1,2],3],4],[[[5,6],7],8]]" to "[[[[1,2],3],4],[[[5,6],7],8]]",
    "[[[[[0,1],2],3],4],[[[5,6],7],8]]" to "[[[[0,3],3],4],[[[5,6],7],8]]",
    "[[1,[2,[3,[4,5]]]],[[[5,6],7],8]]" to "[[1,[2,[7,0]]],[[[10,6],7],8]]",
)

explosions.forEach { a, b ->
    val test = parse(a)
    explode(test)
    if (test.toString() != b) {
        throw Exception("${test} should match ${b}")
    }
}

val sums = mapOf(
    4 to "[[[[1,1],[2,2]],[3,3]],[4,4]]",
    5 to "[[[[3,0],[5,3]],[4,4]],[5,5]]",
    6 to "[[[[5,0],[7,4]],[5,5]],[6,6]]"
)

sums.forEach { a, b ->
    val test = (1..a).map { Pair(Value(it), Value(it)) }

    val result = test.reduce(::sumPairs)

    if (result.toString() != b) {
        throw Exception("${test} should match ${b}")
    }
}

val inputs = File("18.input").readLines()
val nums = inputs.map(::parse)

println(nums.reduce(::sumPairs).magnitude())

inputs.flatMap { a ->
    inputs.flatMap { b -> listOf(sumPairs(parse(a), parse(b)).magnitude(), sumPairs(parse(b), parse(a)).magnitude()) }
}
    .maxOrNull()
    ?.let(::println)


