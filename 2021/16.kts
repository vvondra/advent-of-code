import java.io.File

val bits = File("16.input").readText().trim()
    .map { it.digitToInt(16).toString(2).padStart(4, '0') }
    .joinToString("")

sealed interface Packet
data class Literal(val version: Int, val value: Long) : Packet
data class Operator(val version: Int, val typeId: Int, val packets: List<Packet>) : Packet

fun packet(str: String): Pair<Packet, String> {
    val version = str.take(3).toInt(2)
    val type = str.drop(3).take(3).toInt(2)
    val body = str.drop(6)

    if (type == 4) {
        val (value, tail) = literal(body)
        return Literal(version, value) to tail
    } else {
        val lengthId = body.first()

        when (lengthId) {
            '0' -> {
                val totalLength = body.drop(1).take(15).toInt(2)
                var packetBody = body.drop(16).take(totalLength)
                val packets = mutableListOf<Packet>()
                while (packetBody.isNotEmpty()) {
                    val (packet, packetTail) = packet(packetBody)
                    packets.add(packet)
                    packetBody = packetTail
                }
                return Operator(version, type, packets) to body.drop(16 + totalLength)
            }
            '1' -> {
                val numPackets = body.drop(1).take(11).toInt(2)
                var packetBody = body.drop(12)
                val packets = mutableListOf<Packet>()
                for (i in 0 until numPackets) {
                    val (packet, packetTail) = packet(packetBody)
                    packets.add(packet)
                    packetBody = packetTail
                }
                return Operator(version, type, packets) to packetBody
            }
            else -> throw Exception("nono")
        }
    }
}

fun literal(str: String): Pair<Long, String> {
    val quintuples = str.chunkedSequence(5)
    val tail = quintuples.dropWhile { it.startsWith("1") }
    val value = quintuples.takeWhile { it.startsWith("1") } + tail.take(1)

    return value.map { it.drop(1) }.joinToString("").toLong(2) to tail.drop(1).joinToString("")

}


val (top, _) = packet(bits)

fun versionSum(packet: Packet): Int =
    when (packet) {
        is Literal -> packet.version
        is Operator -> packet.version + packet.packets.map(::versionSum).sum()
        else -> throw Exception("this should be exhaustive when")
    }

fun calc(packet: Packet): Long =
    when (packet) {
        is Literal -> packet.value
        is Operator -> when (packet.typeId) {
            0 -> packet.packets.map(::calc).sum()
            1 -> packet.packets.map(::calc).reduce { acc, l -> acc * l }
            2 -> packet.packets.map(::calc).minOf { it }
            3 -> packet.packets.map(::calc).maxOf { it }
            5 -> if (calc(packet.packets[0]) > calc(packet.packets[1])) 1 else 0
            6 -> if (calc(packet.packets[0]) < calc(packet.packets[1])) 1 else 0
            7 -> if (calc(packet.packets[0]) == calc(packet.packets[1])) 1 else 0
            else -> throw Exception("nono")
        }
        else -> throw Exception("nono")
    }

println(versionSum(top))
println(calc(top))

