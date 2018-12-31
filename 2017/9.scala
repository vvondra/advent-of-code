val stream = io.Source.stdin.mkString.trim.toSeq

case class Parser(depth: Int, score: Int, nonGarbage: Int, garbage: Boolean, ignore: Boolean)


val result = stream.foldLeft(Parser(0, 0, 0, garbage = false, ignore = false)) {
  case (p: Parser, c) => if (p.ignore) {
    p.copy(ignore = false)
  } else c match {
    case '{' if !p.garbage => p.copy(depth = p.depth + 1)
    case '}' if !p.garbage => p.copy(depth = p.depth - 1, score = p.score + p.depth)
    case '!' if p.garbage => p.copy(ignore = true)
    case '<' if !p.garbage => p.copy(garbage = true)
    case '>' => p.copy(garbage = false)
    case _ if p.garbage => p.copy(nonGarbage = p.nonGarbage + 1)
    case _ => p
  }
}


println(result.score)
println(result.nonGarbage)