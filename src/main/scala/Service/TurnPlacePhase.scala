package Service

import GUI.CustomColors

import scala.concurrent.Future
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

  def turnInCards(wm:WorldMap, cards:List[Card]): WorldMap = {
    def awardArmiesIfOwned(countryName:String): Country = {
      val country = wm.getCountry(countryName)
      if(country.owner.get.name == wm.getActivePlayer.get.name)
        country.addArmies(2)
      else
        country
    }

    def isValidCardCombination: Boolean = {
      if(cards.size == 3){
        Unit match {
          case _ if isSameArmyType => true
          case _ if areAllArmyTypesDifferent => true
          case _ => false
        }
      } else false

    }

    def isSameArmyType: Boolean = cards.count(_.armyType == cards.head.armyType) == 3
    def areAllArmyTypesDifferent: Boolean = {
        cards.count(_.armyType == Infantry) == 1 &&
        cards.count(_.armyType == Cavalry) == 1 &&
        cards.count(_.armyType == Artillery) == 1
    }




    if(isValidCardCombination){
      var mutWM = wm
        .updatePlayer(wm.getActivePlayer.get
            .removeCards(cards)
            .addArmies(wm.deckState.armiesToAward))
        .copy(deckState = wm.deckState
            .increaseArmiesToAward
            .copy(cards = wm.deckState.cards ::: cards))

      cards foreach (c => mutWM = mutWM.updateSingleCountry(awardArmiesIfOwned(c.countryName)))

      mutWM
    }
    else  wm

  }


  def allocateTurnArmies(wm: WorldMap): WorldMap = {
    val armiesFromTerritories = calculateArmiesFromTerritories(wm.getActivePlayer.get, wm.countries)
    val armiesFromContinents = calculateArmiesFromContinents(wm.getActivePlayer.get, wm.countries)
    val newPlayer = wm.getActivePlayer.get.addArmies(armiesFromContinents + armiesFromTerritories)
    wm.updatePlayer(newPlayer)
  }

  def calculateArmiesFromTerritories(player: Player, countries: List[Country]): Int = {
    val ownedTerritories = countries.foldLeft(0)((acc, country) => country.owner.map(_.playerNumber).contains(player.playerNumber) ? (acc + 1) | acc)
    ownedTerritories / 3
  }

  def calculateArmiesFromContinents(player: Player, countries: List[Country]): Int = {
    var totalArmies = 0

    val isNorthAmericaOwned = countries.filter(c => CountryFactory.continentLookup("North America").contains(c.name)).forall(_.owner.map(_.name).contains(player.name))
    val isSouthAmericaOwned = countries.filter(c => CountryFactory.continentLookup("South America").contains(c.name)).forall(_.owner.map(_.name).contains(player.name))
    val isEuropeOwned = countries.filter(c => CountryFactory.continentLookup("Europe").contains(c.name)).forall(_.owner.map(_.name).contains(player.name))
    val isAfricaOwned = countries.filter(c => CountryFactory.continentLookup("Africa").contains(c.name)).forall(_.owner.map(_.name).contains(player.name))
    val isAsiaOwned = countries.filter(c => CountryFactory.continentLookup("Asia").contains(c.name)).forall(_.owner.map(_.name).contains(player.name))
    val isAustraliaOwned = countries.filter(c => CountryFactory.continentLookup("Australia").contains(c.name)).forall(_.owner.map(_.name).contains(player.name))

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
    wm.getActivePlayer.get.asInstanceOf[ComputerPlayer].compAsyncDelay.beginTurnDelay(wm)
    wm
  }

  def compPlacementAI(wm:WorldMap): (WorldMap, Future[CompInput]) = {
    val activePlayer = wm.getActivePlayer.get.asInstanceOf[ComputerPlayer]

    if(wm.getActivePlayer.get.armies > 0) {
      val ownedCountries = wm.getCountriesOwnedByPlayer(activePlayer)
      val chosenCountry = ownedCountries(Random.nextInt(ownedCountries.size))
      val wm2 = wm.placeArmies(chosenCountry, activePlayer, 1)
      (wm2, activePlayer.compAsyncDelay.placementDelay(wm2))
    } else {
      (wm, activePlayer.compAsyncDelay.attackSourceDelay(wm))
    }
  }

  def checkEndOfTurnPlacement(wm: WorldMap): Boolean = {
    wm.getActivePlayer.map(_.armies).contains(0)
  }


  def endTurnPlacementPhase(wm: WorldMap): WorldMap = {
    wm.copy(phase = Attacking(None, None))
  }
}
