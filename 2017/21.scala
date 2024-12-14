

val pattern = Seq(
  Seq('.', '#', '.'),
  Seq('.', '.', '#'),
  Seq('#', '#', '#')
)


val a = pattern.transpose
val b = pattern.transpose.map(_.reverse)
val c =pattern.map(_.reverse).reverse

pattern.foreach(c => println(c.mkString))
println
a.foreach(c => println(c.mkString))
println

b.foreach(c => println(c.mkString))
println

c.foreach(c => println(c.mkString))