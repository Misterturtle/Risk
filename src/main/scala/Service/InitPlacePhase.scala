package Service

/**
  * Created by Harambe on 7/20/2017.
  */
object InitPlacePhase{

  def beginTurn(wm:WorldMap):WorldMap ={
    wm.getActivePlayer match {
      case Some(p:HumanPlayer) =>
        wm
      case Some(c:ComputerPlayer) =>
        beginComputerTurn(wm)
    }
  }

  def allocateInitArmies(wm: WorldMap): WorldMap = {
    val armies = 35 - (5 * (wm.players.size - 3))
    wm.copy(players = wm.players.map(p => p.addArmies(armies)))
  }

  def isValidInitPlaceArmyPlacement(wm: WorldMap, player: Player, clickedCountry: Country): Boolean = {
    if (wm.areAllCountriesOwned) {
      clickedCountry.owner.map(_.playerNumber).getOrElse(-1) == wm.getActivePlayer.map(_.playerNumber).getOrElse(-2)
    }
    else {
      clickedCountry.owner.isEmpty
    }
  }

  def nextTurn(wm: WorldMap): WorldMap = {
    if (checkForEndOfInitPlace(wm))
      endInitPhase(wm)
    else{
      beginTurn(wm.setNextActivePlayer())
    }

  }


  def beginComputerTurn(wm: WorldMap): WorldMap = {
    val wm2 = initPlaceCompAI(wm)
    nextTurn(wm2)
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


  def checkForEndOfInitPlace(wm:WorldMap): Boolean = {
    wm.phase == InitialPlacement && wm.noArmiesToPlace
  }

  def endInitPhase(wm: WorldMap): WorldMap = {
    val wm2 = wm.copy(activePlayerNumber = 1, phase = TurnPlacement)
    TurnPlacePhase.beginTurn(wm2)
  }



}
