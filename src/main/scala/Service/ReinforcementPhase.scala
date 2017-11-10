package Service

/**
  * Created by Harambe on 7/20/2017.
  */
object ReinforcementPhase {


  def setReinformentSource(wm: WorldMap, country: Country):WorldMap = wm.copy(phase = wm.phase.asInstanceOf[Reinforcement].copy(source = Some(country)))

  def setReinformentTarget(wm: WorldMap, country: Country):WorldMap = wm.copy(phase = wm.phase.asInstanceOf[Reinforcement].copy(target = Some(country)))

  def isValidSource(wm:WorldMap, country:Country):Boolean = {
    val phase = wm.phase.asInstanceOf[Reinforcement]
    phase.source.isEmpty && country.owner.get.name == wm.getActivePlayer.get.name && country.armies > 1
  }

  def isValidTarget(wm: WorldMap, country: Country): Boolean = {
    val phase = wm.phase.asInstanceOf[Reinforcement]
    if(phase.source.isEmpty)
      false
    else
      phase.source.map(_.adjacentCountries).get.contains(country.name) && country.owner.get.playerNumber == wm.getActivePlayer.get.playerNumber
  }

  def nextTurn(wm:WorldMap) :WorldMap = {

    val wm2 = if (wm.getActivePlayer.get.isCountryTaken) {
      val (ds, card) = wm.deckState.draw()
      wm.updatePlayer(wm.getActivePlayer.get.awardCard(card))
        .setNextActivePlayer()
        .setPhase(TurnPlacement)
        .copy(deckState = ds)
    }
    else {
      wm.setNextActivePlayer()
        .setPhase(TurnPlacement)
    }

    val wm3 = TurnPlacePhase.beginTurn(wm2)
    wm3
  }
}
