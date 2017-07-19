
import scalaz._
import Scalaz._


trait Phase

case object NotInGame extends Phase

case object InitialPlacement extends Phase

case object TurnPlacement extends Phase

case object Transition extends Phase

case object Attacking extends Phase

case object Reinforcement extends Phase



object InitPlacePhase{
  import WorldMap._

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

  def nextInitPlaceTurn(wm: WorldMap): WorldMap = {
    if (checkForEndOfInitPlace(wm))
      endInitPhase(wm)
    else{
      beginActivePlayerTurn(setNextActivePlayer(wm))
    }

  }

  def checkForEndOfInitPlace(wm:WorldMap): Boolean = {
    wm.phase == InitialPlacement && wm.noArmiesToPlace
  }

  def endInitPhase(wm: WorldMap): WorldMap = {
    val wm2 = wm.copy(activePlayerNumber = 1, phase = TurnPlacement)
    beginActivePlayerTurn(wm2)
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

}

object TurnPlacePhase{
  import WorldMap._

  def allocateTurnArmies(wm: WorldMap): WorldMap = {
    val armiesFromTerritories = calculateArmiesFromTerritories(wm.getActivePlayer.get, wm.countries)
    val armiesFromContinents = calculateArmiesFromContinents(wm.getActivePlayer.get, wm.countries)
    val newPlayer = wm.getActivePlayer.map(p => p.addArmies(armiesFromContinents + armiesFromTerritories)).getOrElse(HumanPlayer("None", 0, 0, "black"))
    wm.updatePlayer(newPlayer)
  }

  def calculateArmiesFromTerritories(player: Player, countries: List[Country]): Int = {
    val ownedTerritories = countries.foldLeft(0)((acc, country) => country.owner.map(_.playerNumber).contains(player.playerNumber) ? (acc + 1) | acc)
    ownedTerritories / 3
  }

  def calculateArmiesFromContinents(player: Player, countries: List[Country]): Int = {
    var totalArmies = 0

    val isNorthAmericaOwned = countries.filter(c => CountryFactory.continentLookup("northAmerica").contains(c.name)).forall(_.owner.contains(player))
    val isSouthAmericaOwned = countries.filter(c => CountryFactory.continentLookup("southAmerica").contains(c.name)).forall(_.owner.contains(player))
    val isEuropeOwned = countries.filter(c => CountryFactory.continentLookup("europe").contains(c.name)).forall(_.owner.contains(player))
    val isAfricaOwned = countries.filter(c => CountryFactory.continentLookup("africa").contains(c.name)).forall(_.owner.contains(player))
    val isAsiaOwned = countries.filter(c => CountryFactory.continentLookup("asia").contains(c.name)).forall(_.owner.contains(player))
    val isAustraliaOwned = countries.filter(c => CountryFactory.continentLookup("australia").contains(c.name)).forall(_.owner.contains(player))

    if (isNorthAmericaOwned) totalArmies += 5
    if (isSouthAmericaOwned) totalArmies += 2
    if (isEuropeOwned) totalArmies += 5
    if (isAfricaOwned) totalArmies += 3
    if (isAsiaOwned) totalArmies += 7
    if (isAustraliaOwned) totalArmies += 2

    totalArmies
  }

  def attemptToPlaceArmy(wm:WorldMap, country:Country):WorldMap = {
    if(country.isOwnedBy(wm.activePlayerNumber))
      wm.placeArmies(country, wm.getActivePlayer.get, 1)
    else wm
  }

  def checkEndOfTurnPlacement(wm: WorldMap): Boolean = {
    wm.getActivePlayer.map(_.armies).contains(0)
  }


  def endTurnPlacementPhase(wm: WorldMap): WorldMap = {
    wm.copy(phase = Attacking)
  }

}




case class WorldMap(countries: List[Country], players: List[Player], activePlayerNumber: Int, phase: Phase) {
  def areAllCountriesOwned: Boolean = countries.forall(c => c.owner.nonEmpty)

  def getPlayerByPlayerNumber(playerNumber: Int): Option[Player] = players.find(_.playerNumber == playerNumber)

  def noArmiesToPlace: Boolean = players.forall(_.armies == 0)

  def getActivePlayer: Option[Player] = getPlayerByPlayerNumber(activePlayerNumber)

  def getCountry(name:String): Country = countries.find(_.name == name).getOrElse(throw new Exception)

  def setPhase(phase: Phase): WorldMap = copy(phase = phase)

  def setActivePlayer(playerNumber: Int): WorldMap = copy(activePlayerNumber = playerNumber)

  def updateSingleCountry(country: Country): WorldMap = {
    copy(countries = countries.map(c => (c.name == country.name) ? country | c))
  }

  def updateCountryAndPlayer(country: Country, player: Player) = updateSingleCountry(country).updatePlayer(player)

  def updatePlayer(newPlayer: Player): WorldMap = {
    val newPlayerList = players.map(oldPlayer => if (oldPlayer.playerNumber == newPlayer.playerNumber) newPlayer else oldPlayer)
    copy(players = newPlayerList)
  }

  def updateSomeCountries(updatedCountries: List[Country]): WorldMap = {
    val cMap = countries.map(c => (c.name, c)).toMap
    val ncMap = updatedCountries.map(c => (c.name, c)).toMap
    val ncList = (cMap ++ ncMap).values.toList
    copy(countries = ncList)
  }

  def placeArmies(country: Country, player: Player, amount: Int): WorldMap = updateSingleCountry(country.addArmies(amount).setOwner(player)).updatePlayer(player.removeArmies(amount))
}



object WorldMap {
  import InitPlacePhase._
  import TurnPlacePhase._

  type WorldMapState[A] = State[WorldMap, A]


  def nextTurn(wm: WorldMap): WorldMap = {
    wm.phase match {
      case InitialPlacement => nextInitPlaceTurn(wm)
    }
  }

  def setNextActivePlayer(wm:WorldMap): WorldMap = {
    if(wm.activePlayerNumber == wm.players.size)
      wm.copy(activePlayerNumber = 1)
    else wm.copy(activePlayerNumber = wm.activePlayerNumber + 1)
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
        wm
      case TurnPlacement =>
        TurnPlacePhase.allocateTurnArmies(wm)
    }
  }

  def getNonOwnedCountries(countries: List[Country]): List[Country] = {
    countries.filter(_.owner.isEmpty)
  }

  def getCountriesOwnedByPlayer(player: Player, countries: List[Country]): List[Country] = {
    countries.filter(_.owner.map(_.playerNumber).contains(player.playerNumber))
  }

  def beginComputerTurn(wm: WorldMap): WorldMap = {
    val wm2 = computerPlaceArmy(wm)
    nextTurn(wm2)
  }

  def computerPlaceArmy(wm: WorldMap): WorldMap = {
    wm.phase match {
      case InitialPlacement => InitPlacePhase.initPlaceCompAI(wm)
    }
  }
}
