val changes = io.Source.stdin.getLines.map(_.toInt).toList
var frequencies = scala.collection.mutable.Map[Int, Boolean]()

var frequency = 0
frequencies.put(frequency, true)
var found = false

while (!found) {
  changes.takeWhile(c => {
    frequency += c
    if (frequencies.contains(frequency)) {
      found = true
      false
    } else {
      frequencies.put(frequency, true)
      true
    }
  })
}

println(frequency)