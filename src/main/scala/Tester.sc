def and(secondCond:Boolean): (Boolean) => Boolean = {firstCond:Boolean => firstCond match{
  case firstCond:Boolean =>
    firstCond && secondCond
}

}

true and true