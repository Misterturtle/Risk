package Service

/**
  * Created by Harambe on 7/20/2017.
  */
object AttackingPhase {
  //todo: Currently able to select my own country as a target


  def selectSource(country:Country):Action[WorldMap] = Action { worldMap:WorldMap =>
    if(isValidSourceTarget(worldMap, country))
      worldMap.setPhase(worldMap.phase.asInstanceOf[Attacking].setSource(country))
    else worldMap
  }

  def deselectSource():Action[WorldMap] = Action { worldMap: WorldMap =>
    worldMap.copy(phase = Attacking(None, None))
  }


  def beginBattle(country:Country):Action[WorldMap] = Action { worldMap:WorldMap =>
    val source = worldMap.phase.asInstanceOf[Attacking].source.get
    if(isValidAttackTarget(source, country)){
      worldMap.setPhase(Battle(source, country))
    }
    else worldMap
  }

  def isValidSourceTarget(wm:WorldMap, country:Country):Boolean = {
    country.owner.map(_.playerNumber).contains(wm.activePlayerNumber) && country.armies > 1
  }

  def isValidAttackTarget(source:Country, target:Country) :Boolean = {
    source.owner.map(_.playerNumber).get != target.owner.map(_.playerNumber).get && source.adjacentCountries.contains(target.name)
  }
}
