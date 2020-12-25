fun transform(subject: Long, loopSize: Long): Long {
  val mod = 20201227

  tailrec fun loop(value: Long, loops: Long): Long =
    if (loops == 0L) value
    else loop((value * subject) % mod, loops - 1)

  return loop(subject % mod, loopSize - 1)
}

fun findLoopSize(subject: Long, key: Long): Long {
  val mod = 20201227

  tailrec fun loop(value: Long, loops: Long): Long =
    if (value == key) loops
    else loop((value * subject) % mod, loops + 1)

  return loop(subject % mod, 1)
}

val cardKey = 14012298L
val doorKey = 74241L

val cardLoopSize = findLoopSize(7L, cardKey)
val doorLoopSize = findLoopSize(7L, doorKey)

println(cardLoopSize)
println(doorLoopSize)

val encryptionKey = transform(doorKey, cardLoopSize)
val encryptionKey2 = transform(cardKey, doorLoopSize)

println("$encryptionKey $encryptionKey2")
