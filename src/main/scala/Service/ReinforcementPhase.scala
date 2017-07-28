package Service

/**
  * Created by Harambe on 7/20/2017.
  */
object ReinforcementPhase {


  def setReinformentSource(wm: WorldMap, country: Country):WorldMap = wm.copy(phase = wm.phase.asInstanceOf[Reinforcement].copy(source = Some(country)))

  def setReinformentTarget(wm: WorldMap, country: Country):WorldMap = wm.copy(phase = wm.phase.asInstanceOf[Reinforcement].copy(target = Some(country)))

  def isValidSource(wm:WorldMap, country:Country):Boolean = {
    val phase = wm.phase.asInstanceOf[Reinforcement]
    phase.source.isEmpty && country.owner == wm.getActivePlayer
  }

  def isValidTarget(wm: WorldMap, country: Country): Boolean = {
    val phase = wm.phase.asInstanceOf[Reinforcement]
    if(phase.source.isEmpty)
      false
    else
      phase.source.map(_.adjacentCountries).get.contains(country.name) && country.owner == wm.getActivePlayer
  }

  def nextTurn(wm:WorldMap) :WorldMap = {
    val wm2 = wm.setNextActivePlayer().setPhase(TurnPlacement)
    TurnPlacePhase.beginTurn(wm2)
  }
}
