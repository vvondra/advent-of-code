
sealed class Address {
    class Const(val number: Int) : Address()
    class Register(val add: Char) : Address()
}


data class Memory(val mem: Map<Char, Int>) {
    fun get(a: Address): Int = when(a) {
        is Address.Const -> a.number
        is Address.Register -> mem.getValue(a.add)
    }

    fun set(a: Address.Register, n: Int) = copy(mem + mapOf(a.add to n))

}

fun cycle(input: Int): Int {
    var w: Int = input
    var x: Int = 0
    var y: Int = 0
    var z: Int = 0

    x = x * 0
    x = z % 26
    z = z / 1
    x = x + 14
    x = if (x == w) 1 else 0
    x = if (x == 0) 1 else 0 // x = !x
    y = 0
    y = 25
    y = 25 * x // y = 25x
    y = y + 1 // y = 25x + 1
    z = z * y // z = z (25x + 1)
    y = y * 0 // y = 0
    y = w + y // y = w
    y = y + 1 // y = w + 1
    y = x * y // y = x (w + 1)
    z = z + y

    println(Triple(x, y, z))

    return z
}

(1..9).forEach {
    println(it to cycle(it))
}
