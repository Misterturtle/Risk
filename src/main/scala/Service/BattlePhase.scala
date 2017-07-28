package Service

/**
  * Created by Harambe on 7/20/2017.
  */
object BattlePhase{

  def calculateDefensiveArmies(offArmies:Int, defensiveCountry: Country) = {
    if(offArmies == 1)
      1
    else if (defensiveCountry.armies == 1)
      1
    else 2
  }


  def recordResults(wm:WorldMap, battleResult: BattleResult): WorldMap = {
    wm.copy(phase = wm.phase.asInstanceOf[Battle].copy(previousBattle = Some(battleResult)))
  }

  def removeDeadArmies(wm:WorldMap): WorldMap = {
    val battle = wm.phase.asInstanceOf[Battle]
    val newSourceCountry = battle.source.removeArmies(battle.previousBattle.get.offDefArmiesLost()._1)
    val newTargetCountry = battle.target.removeArmies(battle.previousBattle.get.offDefArmiesLost()._2)
    val newBattle = battle.copy(source = newSourceCountry, target = newTargetCountry)

    wm.copy(phase = newBattle).updateSomeCountries(List(newSourceCountry, newTargetCountry))
  }

  def handleIfCountryConquered(wm:WorldMap):WorldMap = {
    val battle = wm.phase.asInstanceOf[Battle]
    if(battle.target.armies == 0){
      val conqueredCountry = battle.target.copy(owner = wm.getActivePlayer)
      wm.setPhase(battle.copy(target = conqueredCountry, isTransferring = true)).updateSingleCountry(conqueredCountry)
    }
    else wm
  }
}
