

val votes = Seq(("scala", 1), ("java", 4), ("scala", 10), ("scala", 1), ("python", 10))

votes.groupBy(_._1)

var orderedVotes = votes
  .groupBy(_._1)
  .map {
    case (which, count) => (which, count.foldLeft(0)(_ + _._2))
  }.toSeq
  .sortBy(_._2)
  .reverse