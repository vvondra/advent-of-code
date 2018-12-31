import scala.annotation.tailrec

val programs = io.Source.stdin.getLines
  .map(_.split("<->").map(_.trim))
  .map { case Array(source, dest) => source -> dest.split(",").map(_.trim).toSeq }
  .toMap

def componentCount(connections: Map[String, Seq[String]]): Int = {
  @tailrec
  def exploreComponents(unvisited: Map[String, Seq[String]], components: Set[Set[String]]): Set[Set[String]] = {
    if (unvisited.isEmpty) {
      components
    } else {
      val c = component(unvisited.keys.head, connections)
      exploreComponents(unvisited -- c, components + c)
    }
  }

  exploreComponents(connections, Set.empty).size
}

def component(current: String, connections: Map[String, Seq[String]]): Set[String] = {
  def recurse(current: String, visited: Set[String]): Set[String] = {
    if (visited.contains(current))
      visited
    else {
      connections(current)
        .filterNot(visited.contains)
        .foldLeft(visited + current)((vis, n) => recurse(n, vis))
    }
  }

  recurse(current, Set.empty)
}

println(component("0", programs).size)
println(componentCount(programs))