package Service

import GUI.CustomColors

import scala.util.Random
import scalaz.Scalaz._

/**
  * Created by Harambe on 7/20/2017.
  */
object TurnPlacePhase{

  def beginTurn(wm:WorldMap):WorldMap = {
    val wm2 = allocateTurnArmies(wm)

    wm2.getActivePlayer match {
      case Some(p:HumanPlayer) =>
        wm2
      case Some(c:ComputerPlayer) =>
        beginCompTurn(wm2)
    }
  }

  def allocateTurnArmies(wm: WorldMap): WorldMap = {
    val armiesFromTerritories = calculateArmiesFromTerritories(wm.getActivePlayer.get, wm.countries)
    val armiesFromContinents = calculateArmiesFromContinents(wm.getActivePlayer.get, wm.countries)
    val newPlayer = wm.getActivePlayer.map(p => p.addArmies(armiesFromContinents + armiesFromTerritories)).getOrElse(HumanPlayer("None", 0, 0, CustomColors.gray))
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

  def beginCompTurn(wm:WorldMap):WorldMap = {
    val wm2 = compPlacementAI(wm)
    ReinforcementPhase.nextTurn(wm2)
  }

  def compPlacementAI(wm:WorldMap):WorldMap = {
    val ownedCountries = wm.getCountriesOwnedByPlayer(wm.getActivePlayer.get)
    var mutatingWorldMap = wm

    for(a<-0 until wm.getActivePlayer.map(_.armies).getOrElse(0)){
      val chosenCountry = ownedCountries(Random.nextInt(ownedCountries.size))
      mutatingWorldMap = mutatingWorldMap.placeArmies(chosenCountry, wm.getActivePlayer.get, 1)
    }
    mutatingWorldMap
  }

  def checkEndOfTurnPlacement(wm: WorldMap): Boolean = {
    wm.getActivePlayer.map(_.armies).contains(0)
  }


  def endTurnPlacementPhase(wm: WorldMap): WorldMap = {
    wm.copy(phase = Attacking(None, None))
  }
}
