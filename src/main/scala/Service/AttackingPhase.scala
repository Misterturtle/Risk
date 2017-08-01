package Service

/**
  * Created by Harambe on 7/20/2017.
  */
object AttackingPhase {
  //todo: Currently able to select my own country as a target


  def selectSource(wm:WorldMap, country:Country):WorldMap = {
    println("Source selected")
    if(isValidSourceTarget(wm, country))
      wm.setPhase(wm.phase.asInstanceOf[Attacking].setSource(country))
    else wm
  }

  def deselectSource(wm:WorldMap):WorldMap = {
    wm.copy(phase = Attacking(None))
  }


  def beginBattle(wm:WorldMap, country:Country):WorldMap = {
    if(isValidAttackTarget(wm.phase.asInstanceOf[Attacking].source.get, country)){
      val source = wm.phase.asInstanceOf[Attacking].source.get
      wm.setPhase(Battle(source, country))
    }
    else wm
  }

  def isValidSourceTarget(wm:WorldMap, country:Country):Boolean = {
    country.owner.map(_.playerNumber).contains(wm.activePlayerNumber) && country.armies > 1
  }

  def isValidAttackTarget(source:Country, target:Country) :Boolean = {
    source.owner != target.owner && source.adjacentCountries.contains(target.name)
  }
}
