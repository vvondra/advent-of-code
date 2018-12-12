import scala.sys.process._

case class Point(px: Int, py: Int, vx: Int, vy: Int)
val pattern = raw"position=<\s*(-?\d+),\s*(-?\d+)> velocity=<\s*(-?\d+),\s*(-?\d+)>".r
val initial = io.Source.stdin.getLines
  .map { case pattern(px, py, vx, vy) => Point(px.toInt, py.toInt, vx.toInt, vy.toInt) }
  .toVector

def clear() = "clear".!

def screen = for {
  y <- 160 to 220
  x <- 80 to 200
} yield (x, y)

def printSky(points: Seq[Point]): Unit = screen
  .foreach(row => {
    if (points.exists(p => p.px == row._1 && p.py == row._2)) {
      print("#")
    } else {
      print(".")
    }
    if (row._1 == 200) println()
  })

def move(points: Seq[Point]): Seq[Point] = points.map(p => Point(p.px + p.vx, p.py + p.vy, p.vx, p.vy))

var sky: Seq[Point] = initial

var i = 0
do {
  i = i + 1
  sky = move(sky)
} while (sky.count(p => (100 to 200).contains(p.px) && (160 to 220).contains(p.py)) < 10)


(1 to 100).foreach(_ => {
  println(i)
  printSky(sky)
  sky = move(sky)
  i = i + 1
  Thread.sleep(200)
  clear()
})