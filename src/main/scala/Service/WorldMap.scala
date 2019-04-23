package Service

import scalaz.Scalaz._
import common.Common._

/**
  * Created by Harambe on 7/20/2017.
  */
case object WorldMap {
  def setArmiesForPlayer(playerNumber: Int, armiesAmount: Int)(worldMap: WorldMap): WorldMap = {
    val updatedPlayers = worldMap.players
      .map {
        case player if player.playerNumber == playerNumber =>
          player >> Player.setArmies(armiesAmount)

        case player => player
      }

    worldMap.copy(players = updatedPlayers)
  }

  val EMPTY = new WorldMap(Nil, Nil, 0, null)
  val INITIAL = new WorldMap(CountryFactory.getCountries, Nil, 0, NotInGame)

  def removeAllArmiesFromPlayer(playerNumber: Int)(worldMap: WorldMap): WorldMap = {
    val updatedPlayers = worldMap.players
      .map {
        case player if player.playerNumber == playerNumber => player >> Player.removeAllArmies
        case player => player
      }

    worldMap.copy(players = updatedPlayers)
  }

  def updatePlayer(player: Player)(worldMap: WorldMap): WorldMap = {
    val newPlayerList = worldMap.players.map(oldPlayer => if (oldPlayer.playerNumber == player.playerNumber) player else oldPlayer)
    worldMap.copy(players = newPlayerList)
  }
}

case class WorldMap(countries: List[Country], players: List[Player], activePlayerNumber: Int, phase: Phase) {

  def setNextActivePlayer(): WorldMap = {
    if (activePlayerNumber == players.size)
      copy(activePlayerNumber = 1)
    else copy(activePlayerNumber = activePlayerNumber + 1)
  }

  def getNonOwnedCountries(): List[Country] = {
    countries.filter(_.owner.isEmpty)
  }

  def getCountriesOwnedByPlayer(player: Player): List[Country] = {
    countries.filter(_.owner.map(_.playerNumber).contains(player.playerNumber))
  }

  def areAllCountriesOwned: Boolean = countries.forall(c => c.owner.nonEmpty)

  def getPlayerByPlayerNumber(playerNumber: Int): Option[Player] = players.find(_.playerNumber == playerNumber)

  def noArmiesToPlace: Boolean = players.forall(_.armies == 0)

  def getActivePlayer: Option[Player] = getPlayerByPlayerNumber(activePlayerNumber)

  def getCountry(name: String): Country = {
    countries.find(_.name.toLowerCase == name.toLowerCase()).getOrElse(throw new Exception)
  }

  def setPhase(phase: Phase): WorldMap = {
    copy(phase = phase)
  }

  def setActivePlayer(playerNumber: Int): WorldMap = copy(activePlayerNumber = playerNumber)

  def updateSingleCountry(country: Country): WorldMap = {
    copy(countries = countries.map(c => (c.name == country.name) ? country | c))
  }

  def transferArmies(source: Country, target: Country, amount: Int): WorldMap = updateSomeCountries(List(source.removeArmies(amount), target.addArmies(amount)))

  def updateCountryAndPlayer(country: Country, player: Player) = updateSingleCountry(country).updatePlayer(player)

  def updatePlayer(newPlayer: Player): WorldMap = {
    val newPlayerList = players.map(oldPlayer => if (oldPlayer.playerNumber == newPlayer.playerNumber) newPlayer else oldPlayer)
    copy(players = newPlayerList)
  }

  def updateSomeCountries(updatedCountries: List[Country]): WorldMap = {
    val countryMap = countries.map(c => (c.name, c)).toMap
    val updatedCountryMap = updatedCountries.map(c => (c.name, c)).toMap
    val newCountriesList = (countryMap ++ updatedCountryMap).values.toList
    copy(countries = newCountriesList)
  }

  def placeArmies(country: Country, player: Player, amount: Int): WorldMap = updateSingleCountry(country.addArmies(amount).setOwner(player)).updatePlayer(player.removeArmies(amount))
}
