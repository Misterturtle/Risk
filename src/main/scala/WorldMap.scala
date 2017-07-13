import scalaz._
import Scalaz._



trait Phase
case object NotInGame extends Phase
case object InitialPlacement extends Phase

case class WorldMap(countries: List[Country], players: List[Player], activePlayer: Option[Player], phase: Phase, stateStamp:StateStamp) extends Stateful{
  def areAllCountriesOwned: Boolean = countries.forall(c => c.owner.nonEmpty)
}

object WorldMap {
  type WorldMapState[A] = State[WorldMap, A]
  type Mutation = (StateStamp) => Validation



  def beginActivePlayerTurn(wm:WorldMap): WorldMap = {
    wm.activePlayer match {
      case Some(p:HumanPlayer) => beginHumanTurn(wm)
      case Some(c:ComputerPlayer) => beginComputerTurn(wm)
    }
  }

  def beginHumanTurn(wm:WorldMap):WorldMap = {
    wm.phase match{
      case InitialPlacement =>
        beginHumanInitPlaceTurn(wm)
    }
  }

  def beginHumanInitPlaceTurn(wm:WorldMap): WorldMap = {
    if(wm.areAllCountriesOwned){
      val countries = getCountriesOwnedByPlayer(wm.activePlayer.get, wm.countries)
      mutateCountriesClickActionUnsafe(countries, StateTransitions.initPlaceClickAction(wm.activePlayer.get.asInstanceOf[HumanPlayer]))
      updateCountries(wm, countries)
    }
    else {
      val countries = getNonOwnedCountries(wm.countries)
      mutateCountriesClickActionUnsafe(countries, StateTransitions.initPlaceClickAction(wm.activePlayer.get.asInstanceOf[HumanPlayer]))
      updateCountries(wm, countries)
    }
  }

  def mutateCountriesClickActionUnsafe(countries:List[Country], clickAction: (Country) => Unit):Unit = {
    countries.foreach{c => c.setOnClick(() => clickAction(c))}
  }

  def getNonOwnedCountries(countries:List[Country]): List[Country] = {
    countries.filter(_.owner.isEmpty)
  }

  def getCountriesOwnedByPlayer(player:Player, countries:List[Country]): List[Country] = {
    countries.filter(_.owner.contains(player))
  }

  def updateCountries(wm:WorldMap, updatedCountries: List[Country]): WorldMap = {
    val cMap = wm.countries.map(c => (c.name, c)).toMap
    val ncMap = updatedCountries.map(c => (c.name, c)).toMap
    val ncList = (cMap ++ ncMap).values.toList
    wm.copy(countries = ncList)
  }

  def beginComputerTurn(wm:WorldMap):WorldMap = ???




  def setPhase(wm:WorldMap, phase:Phase): WorldMap = wm.copy(phase = phase)
  def setActivePlayer(wm: WorldMap, player:Option[Player]): WorldMap = wm.copy(activePlayer = player)

  def allocateInitArmies(wm: WorldMap): WorldMap = {
    val armies = 35 - (5 * (wm.players.size - 3))
    wm.copy(players = wm.players.map(p => p.addArmies(armies)))
  }


  def placeArmies(country: Country, player: Player, amount: Int): (Country, Player) = (country.addArmies(amount), player.removeArmies(amount))


}
