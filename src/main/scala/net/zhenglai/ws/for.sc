
val provinces = List("Jiangsu", "Zhejiang", "Shanghai")

for {
  p <- provinces
  c <- p
  if c.isLower
} yield s"$c - ${c.toUpper}"


provinces.flatMap(_.toSeq.withFilter(_.isLower) map (c => s"$c - ${c.toUpper}"))


