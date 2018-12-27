// On a Mac, run with DYLIB_PATH=. scala -classpath 'com.microsoft.z3.jar:.' 23.scala < 23.input
// Needs the compiled z3 dylib files and jar file in the same directory

import com.microsoft.z3.{ArithExpr, Context}

import scala.collection.JavaConverters._

case class Bot(pos: (Int, Int, Int), range: Int)

val bots = io.Source.stdin.getLines
  .map { l => l.split(", ") }
  .map { l => (l.head.drop(5).dropRight(1).split(",").map(_.toInt).toSeq, l.last.split("=").last.toInt) }
  .map { case (Seq(x, y, z), r) => Bot((x, y, z), r) }
  .toList

def dist(a: Bot, b: Bot) = Math.abs(a.pos._1 - b.pos._1) + Math.abs(a.pos._2 - b.pos._2) + Math.abs(a.pos._3 - b.pos._3)

val strongest = bots.maxBy(_.range)

println(bots.count(dist(_, strongest) <= strongest.range))


val ctx = new Context(Map("model" -> "true").asJava)
val optimize = ctx.mkOptimize()

val (x, y, z) = (ctx.mkIntConst("x"), ctx.mkIntConst("y"), ctx.mkIntConst("z"))
val (dist, inRange) = (ctx.mkIntConst("dist"), ctx.mkIntConst("sum"))
val rangeChecks = bots.zipWithIndex.map { case (_, i) => ctx.mkIntConst(s"rangeCheck$i") }

def abs(x: ArithExpr): ArithExpr = ctx.mkITE(ctx.mkLt(x, ctx.mkInt(0)), ctx.mkMul(ctx.mkInt(-1), x), x).asInstanceOf[ArithExpr]

bots.zipWithIndex.foreach {
  case (bot, i) =>
    val manhattanDistance = ctx.mkAdd(
      abs(ctx.mkSub(x, ctx.mkInt(bot.pos._1))),
      abs(ctx.mkSub(y, ctx.mkInt(bot.pos._2))),
      abs(ctx.mkSub(z, ctx.mkInt(bot.pos._3)))
    )

    optimize.Add(
      ctx.mkEq(
        rangeChecks(i),
        ctx.mkITE(ctx.mkLe(manhattanDistance, ctx.mkInt(bot.range)), ctx.mkInt(1), ctx.mkInt(0))
      )
    )
}
optimize.Add(ctx.mkEq(inRange, ctx.mkAdd(rangeChecks: _*)))
optimize.Add(ctx.mkEq(dist, ctx.mkAdd(x, y, z)))
val mostIntersecting = optimize.MkMaximize(inRange)
val closest = optimize.MkMinimize(dist)


println(optimize.Check())
println(s"intersections=${mostIntersecting.getUpper}")
println(s"distance=${closest.getLower}")
println(s"x=${optimize.getModel.getConstInterp(x)}")
println(s"y=${optimize.getModel.getConstInterp(y)}")
println(s"z=${optimize.getModel.getConstInterp(z)}")
