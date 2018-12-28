var polymer = io.Source.stdin.mkString.trim

def react(s: String) = s.toSeq.foldLeft(List.empty[Char]) {
    case (head :: tail, char) if head.toUpper == char.toUpper && head != char => tail
    case (list, char) => char :: list
  }.reverse.mkString("")

var reduced = react(polymer)
val reducedB = ('a' to 'z').map(c => react(reduced.replaceAll("(?i)" + c, ""))).minBy(_.length)

println(reduced.length)
println(reducedB.length)