import scala.collection.mutable

case class C(x: BigInt, y: BigInt)

val Depth = 8112
val Target = C(13, 743)
val Modulo = 20183
val Zero = BigInt(0)

def memoize[I, O](f: I => O): I => O = new mutable.HashMap[I, O]() {
  override def apply(key: I) = getOrElseUpdate(key, f(key))
}

lazy val geologicIndex: C => BigInt = memoize {
  case C(Zero, Zero) => 0
  case Target => 0
  case C(x, Zero) => x * 16807 mod Modulo
  case C(Zero, y) => y * 48271 mod Modulo
  case C(x, y) => erosionLevel(C(x, y - 1)) * erosionLevel(C(x - 1, y))
}

def erosionLevel(c: C) = (geologicIndex(c) + Depth) mod Modulo
def riskLevel(c: C) = erosionLevel(c) mod 3

val minSquare = for {
  i <- Zero to Target.x
  j <- Zero to Target.y
} yield C(i, j)

println(minSquare.map(riskLevel).sum)

// Part 2

sealed trait Tool
case object Torch extends Tool
case object Gear extends Tool
case object Neither extends Tool
case class Vertex(c: C, tool: Tool)
case class VertexWithCost(v: Vertex, cost: Int) extends Ordered[VertexWithCost] {
  override def compare(that: VertexWithCost) = that.cost - this.cost
}

def cost(from: Vertex, to: Vertex): Int = if (from.tool == to.tool) 1 else 7
def tools(riskLevel: BigInt): Set[Tool] = riskLevel.toLong match {
  case 0 => Set(Torch, Gear)
  case 1 => Set(Gear, Neither)
  case 2 => Set(Torch, Neither)
}
def otherTool(v: Vertex): Tool = tools(riskLevel(v.c)).filterNot(_ == v.tool).head

def neighbours(v: Vertex): Set[Vertex] = {
  Seq((-1, 0), (1, 0), (0, 1), (0, -1))
    .map { case (dx, dy) => C(v.c.x + dx, v.c.y + dy) }
    .filter { case C(x, y) => x >= 0 && y >= 0 }
    .map { c => Vertex(c, v.tool) }
    .filter { c => tools(riskLevel(c.c)).contains(c.tool) }
    .toSet ++ Set(v.copy(tool = otherTool(v)))
}

def shortestPath(): Unit = {
  val target = Vertex(Target, Torch)
  val start = Vertex(C(Zero, Zero), Torch)
  val frontier = mutable.PriorityQueue(VertexWithCost(start, 0))
  val distance = mutable.Map[Vertex, Int](start -> 0)
  val prev = mutable.Map[Vertex, Vertex]()
  val visited = mutable.Set[Vertex]()
  var enqueue = true

  while (frontier.nonEmpty) {
    val u = frontier.dequeue().v
    if (!visited.contains(u)) {
      visited.add(u)
      neighbours(u).foreach(v => {
        val alt = distance(u) + cost(u, v)
        if (alt < distance.getOrElse(v, Int.MaxValue)) {
          distance.put(v, alt)
          prev.put(v, u)

          if (v == target) {
            enqueue = false
          } else {
            if (enqueue) {
              frontier.enqueue(VertexWithCost(v, alt))
            }
          }
        }
      })
    }
  }

  distance(target)
}

shortestPath()