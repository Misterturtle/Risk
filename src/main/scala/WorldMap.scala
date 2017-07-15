import scalafx.scene.paint.Color
import scalaz._
import Scalaz._


trait Phase

case object NotInGame extends Phase

case object InitialPlacement extends Phase

case object TurnPlacement extends Phase

case object Transition extends Phase

case class WorldMap(countries: List[Country], players: List[Player], activePlayerNumber: Int, phase: Phase, stateStamp: StateStamp) extends Stateful {
  def areAllCountriesOwned: Boolean = countries.forall(c => c.owner.nonEmpty)

  def getPlayerByPlayerNumber(playerNumber: Int): Option[Player] = players.find(_.playerNumber == playerNumber)

  def noArmiesToPlace: Boolean = players.forall(_.armies == 0)

  def getActivePlayer: Option[Player] = getPlayerByPlayerNumber(activePlayerNumber)
}

object WorldMap {
  type WorldMapState[A] = State[WorldMap, A]
  type Mutation = (StateStamp) => Validation


  def nextTurn(wm: WorldMap): WorldMap = {
    wm.phase match {
      case InitialPlacement => nextInitPlaceTurn(wm)
    }
  }

  //todo: refactor this ugly mess
  def nextInitPlaceTurn(wm: WorldMap): WorldMap = {
    val wm2 = removeCountriesClickActions(wm)
    if (wm2.activePlayerNumber == wm.players.size) {
      if (wm2.noArmiesToPlace) {
        endInitPhase(wm2)
      }
      else {
        val wm3 = wm2.copy(activePlayerNumber = 1)
        beginActivePlayerTurn(wm3)
      }
    }
    else {
      val wm3 = wm2.copy(activePlayerNumber = wm2.activePlayerNumber + 1)
      beginActivePlayerTurn(wm3)
    }
  }

  def endInitPhase(wm:WorldMap):WorldMap = {
    val wm2 = wm.copy(activePlayerNumber = 1, phase = TurnPlacement)
    beginActivePlayerTurn(wm2)
  }



  def removeCountriesClickActions(wm: WorldMap): WorldMap = {
    val noClickCountries = setCountriesClickAction(wm.countries, (_) => {})
    wm.copy(countries = noClickCountries)
  }

  def beginActivePlayerTurn(wm: WorldMap): WorldMap = {
    wm.getActivePlayer match {
      case Some(p: HumanPlayer) => beginHumanTurn(wm)
      case Some(c: ComputerPlayer) => beginComputerTurn(wm)
    }
  }

  def beginHumanTurn(wm: WorldMap): WorldMap = {
    wm.phase match {
      case InitialPlacement =>
        beginHumanInitPlaceTurn(wm)
      case TurnPlacement =>
        val wm2 = allocateTurnArmies(wm)
        val clickableCountries = setCountriesClickAction(wm2.countries, StateTransitions.turnPlacementClickAction(_, wm2.getPlayerByPlayerNumber(wm2.activePlayerNumber).get))
        wm2.copy(countries = clickableCountries)
    }
  }



  def allocateTurnArmies(wm:WorldMap):WorldMap = {
    val armiesFromTerritories = calculateArmiesFromTerritories(wm.getActivePlayer.get, wm.countries)
    val armiesFromContinents = calculateArmiesFromContinents(wm.getActivePlayer.get, wm.countries)
    val newPlayer = wm.getActivePlayer.map(p => p.addArmies(armiesFromContinents + armiesFromTerritories)).getOrElse(HumanPlayer(0,0, "black"))
    updatePlayer(wm, newPlayer)
  }

  def calculateArmiesFromTerritories(player:Player, countries:List[Country]): Int = {
    val ownedTerritories = countries.foldLeft(0)((acc,country) => country.owner.contains(player) ? (acc + 1) | acc)
    ownedTerritories / 3
  }

  def calculateArmiesFromContinents(player:Player, countries:List[Country]): Int = {
    var totalArmies = 0

    val isNorthAmericaOwned = countries.filter(c => CountryFactory.continentLookup("northAmerica").contains(c.name)).forall(_.owner.contains(player))
    val isSouthAmericaOwned = countries.filter(c => CountryFactory.continentLookup("southAmerica").contains(c.name)).forall(_.owner.contains(player))
    val isEuropeOwned = countries.filter(c => CountryFactory.continentLookup("europe").contains(c.name)).forall(_.owner.contains(player))
    val isAfricaOwned = countries.filter(c => CountryFactory.continentLookup("africa").contains(c.name)).forall(_.owner.contains(player))
    val isAsiaOwned = countries.filter(c => CountryFactory.continentLookup("asia").contains(c.name)).forall(_.owner.contains(player))
    val isAustraliaOwned = countries.filter(c => CountryFactory.continentLookup("australia").contains(c.name)).forall(_.owner.contains(player))

    if(isNorthAmericaOwned) totalArmies += 5
    if(isSouthAmericaOwned) totalArmies += 2
    if(isEuropeOwned) totalArmies += 5
    if(isAfricaOwned) totalArmies += 3
    if(isAsiaOwned) totalArmies += 7
    if(isAustraliaOwned) totalArmies += 2

    totalArmies
  }

  def updatePlayer(wm:WorldMap, newPlayer:Player):WorldMap = {
    val newPlayerList = wm.players.map(oldPlayer => if(oldPlayer.playerNumber == newPlayer.playerNumber) newPlayer else oldPlayer)
    wm.copy(players = newPlayerList)
  }

  def beginHumanInitPlaceTurn(wm: WorldMap): WorldMap = {
    if (wm.areAllCountriesOwned) {
      val countries = getCountriesOwnedByPlayer(wm.getActivePlayer.get, wm.countries)
      val onClickCountries = setCountriesClickAction(countries, StateTransitions.initPlaceClickAction(wm.getActivePlayer.get.asInstanceOf[HumanPlayer]))
      updateCountries(wm, onClickCountries)
    }
    else {
      val countries = getNonOwnedCountries(wm.countries)
      val onClickCountries = setCountriesClickAction(countries, StateTransitions.initPlaceClickAction(wm.getActivePlayer.get.asInstanceOf[HumanPlayer]))
      updateCountries(wm, onClickCountries)
    }
  }

  def setCountriesClickAction(countries: List[Country], clickAction: (Country) => Unit): List[Country] = {
    countries.map { c => c.setOnClick(() => clickAction(c)) }
  }

  def getNonOwnedCountries(countries: List[Country]): List[Country] = {
    countries.filter(_.owner.isEmpty)
  }

  def getCountriesOwnedByPlayer(player: Player, countries: List[Country]): List[Country] = {
    countries.filter(_.owner.map(_.playerNumber).contains(player.playerNumber))
  }

  def updateCountries(wm: WorldMap, updatedCountries: List[Country]): WorldMap = {
    val cMap = wm.countries.map(c => (c.name, c)).toMap
    val ncMap = updatedCountries.map(c => (c.name, c)).toMap
    val ncList = (cMap ++ ncMap).values.toList
    wm.copy(countries = ncList)
  }

  def beginComputerTurn(wm: WorldMap): WorldMap = {
    val wm2 = computerPlaceArmy(wm)
    nextTurn(wm2)
  }

  def computerPlaceArmy(wm:WorldMap):WorldMap = {
    wm.phase match {
      case InitialPlacement => initPlaceCompAI(wm)
    }
  }

  def initPlaceCompAI(wm:WorldMap): WorldMap = {
    if(wm.areAllCountriesOwned){
      val chosenCountry = wm.countries.find(_.owner.map(_.playerNumber).contains(wm.getActivePlayer.map(_.playerNumber).getOrElse(-100))).get
      val newCountryPlayer = placeArmies(chosenCountry, wm.getActivePlayer.get, 1)
      val wm2 = updateSingleCountry(wm, newCountryPlayer._1)
      updatePlayer(wm2, newCountryPlayer._2)
    }
    else {
      val chosenCountry = wm.countries.find(_.owner.isEmpty).get
      val newCountryPlayer = placeArmies(chosenCountry, wm.getActivePlayer.get, 1)
      val wm2 = updateSingleCountry(wm, newCountryPlayer._1)
      updatePlayer(wm2, newCountryPlayer._2)
    }
  }

  def updateSingleCountry(wm:WorldMap, country: Country): WorldMap = {
    wm.copy(countries = wm.countries.map(c => (c.name == country.name) ? country | c))
  }




  def setPhase(wm: WorldMap, phase: Phase): WorldMap = wm.copy(phase = phase)

  def setActivePlayer(wm: WorldMap, playerNumber: Int): WorldMap = wm.copy(activePlayerNumber = playerNumber)

  def allocateInitArmies(wm: WorldMap): WorldMap = {
    val armies = 35 - (5 * (wm.players.size - 3))
    wm.copy(players = wm.players.map(p => p.addArmies(armies)))
  }


  def placeArmies(country: Country, player: Player, amount: Int): (Country, Player) = (country.addArmies(amount).setOwner(player), player.removeArmies(amount))


}
