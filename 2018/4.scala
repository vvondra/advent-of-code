import java.time.{Duration, LocalDate, LocalTime}

sealed trait Action
case object ShiftStart extends Action
case object Sleep extends Action
case object Wakeup extends Action
case class Event(date: LocalDate, time: LocalTime, guardId: Int, action: Action)
case class Stats(guardId: Int, stats: GuardStats)
case class GuardStats(minutesSlept: Long, sleptPerMinute: List[Int])

val pattern = raw"\[(\d{4}-\d{2}-\d{2}) (\d{2}:\d{2})\] (.+)".r
val guardPattern = raw"Guard #(\d+) begins shift".r
val midnight = LocalTime.parse("00:00")

val events = io.Source.stdin.getLines
  .toList
  .sorted
  .foldLeft((0, List[Event]())) {
    case ((currentGuardId, guardEvents), log) =>
      val event = log match {
        case pattern(logDate, logTime, action) =>
          val date = LocalDate.parse(logDate)
          val time = LocalTime.parse(logTime)
          if (action == "wakes up") {
            Event(date, time, currentGuardId, Wakeup)
          } else if (action == "falls asleep") {
            Event(date, time, currentGuardId, Sleep)
          } else {
            action match {
              case guardPattern(newGuardId) =>
                if (time.getHour == 23) {
                  Event(date.plusDays(1), midnight, newGuardId.toInt, ShiftStart)
                } else {
                  Event(date, time, newGuardId.toInt, ShiftStart)
                }
            }
          }
      }

      (event.guardId, guardEvents :+ event)
  }
  ._2

val aggregated = events.groupBy(_.guardId)
  .map {
    case (guardId, guardEvents) =>
      val totalSlept = guardEvents.foldLeft((Option.empty[Event], 0l, List.fill[Int](60)(0))) {
        case ((previousEvent, slept, minutes), event) =>
          event match {
            case Event(_, wakeupTime, _, Wakeup) =>
              val stats = (previousEvent.get.time.getMinute until wakeupTime.getMinute).foldLeft(minutes) {
                case (m, minute) => m.updated(minute, m(minute) + 1)
              }

              (Some(event), slept + Duration.between(previousEvent.get.time, wakeupTime).toMinutes, stats)
            case Event(_, _, _, _) => (Some(event), slept, minutes)
          }
      }

      Stats(guardId, GuardStats(totalSlept._2, totalSlept._3))
  }


val sleepiest = aggregated.map {
    case Stats(guardId, GuardStats(totalSlept, minuteFrequency)) =>
      (guardId, totalSlept, minuteFrequency.zipWithIndex.maxBy(_._1)._2)
  }
  .maxBy(_._2)

println(sleepiest)
println(sleepiest._1 * sleepiest._3)


val sleepiestAtMinute = aggregated
  .maxBy(_.stats.sleptPerMinute.max)

println(sleepiestAtMinute)
println(sleepiestAtMinute.guardId * sleepiestAtMinute.stats.sleptPerMinute.zipWithIndex.maxBy(_._1)._2)