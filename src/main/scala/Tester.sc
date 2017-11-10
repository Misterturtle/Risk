for (b <- 0 to 5) {
  println()
  for (a <- 0 to 6) {
    val startValue = 6-b
    val loopMultiple = 6*a
    print(startValue + loopMultiple - 1 + " ")
  }
}