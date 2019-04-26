package Service

import common.Common._

object InitPlacePhase {

  val setInitialPhase: Action[Phase] = Action { _:Phase =>
    InitialPlacement
  }

  val endInitPhase: Action[WorldMap] = Action { worldMap:WorldMap =>
    val wm2 = worldMap.copy(activePlayerNumber = 1, phase = TurnPlacement)
    TurnPlacePhase.beginTurn(wm2)
  }

  val beginTurn: Action[WorldMap] = Action { worldMap:WorldMap =>
    worldMap.getActivePlayer match {
      case Some(p: HumanPlayer) =>
        worldMap
      case Some(c: ComputerPlayer) =>
        beginComputerTurn(worldMap)
    }
  }

  val nextTurn: Action[WorldMap] = FlatAction { wm: WorldMap =>
    if (checkForEndOfInitPlace(wm))
      endInitPhase
    else {
      wm.setNextActivePlayer() >>
      beginTurn
    }
  }

  def allocateInitArmies(): Action[List[Player]] = Action { listOfPlayers: List[Player] =>
      val armies = 35 - (5 * (listOfPlayers.size - 3))
      listOfPlayers.map(p => p.addArmies(armies))
  }

  def isValidInitPlaceArmyPlacement(worldMap:WorldMap, player: Player, clickedCountry: Country): Boolean = {
    if (worldMap.areAllCountriesOwned) {
      clickedCountry.owner.map(_.playerNumber).getOrElse(-1) == worldMap.getActivePlayer.map(_.playerNumber).getOrElse(-2)
    }
    else {
      clickedCountry.owner.isEmpty
    }
  }

  def beginComputerTurn(wm: WorldMap): WorldMap = {
    initPlaceCompAI(wm) >>
    nextTurn
  }


  def initPlaceCompAI(wm: WorldMap): WorldMap = {
    if (wm.areAllCountriesOwned) {
      val chosenCountry = wm.countries.find(_.owner.map(_.playerNumber).contains(wm.getActivePlayer.map(_.playerNumber).getOrElse(-100))).get
      wm.placeArmies(chosenCountry, wm.getActivePlayer.get, 1)

    }
    else {
      val chosenCountry = wm.countries.find(_.owner.isEmpty).get
      wm.placeArmies(chosenCountry, wm.getActivePlayer.get, 1)
    }
  }


  def checkForEndOfInitPlace(wm: WorldMap): Boolean = {
    wm.phase == InitialPlacement && wm.noArmiesToPlace
  }

}
