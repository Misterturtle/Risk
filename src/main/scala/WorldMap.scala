
import scala.collection.mutable.ListBuffer
import scala.util.Random
import scalaz._
import Scalaz._


trait Phase

case object NotInGame extends Phase

case object InitialPlacement extends Phase

case object TurnPlacement extends Phase

case class Attacking(source:Option[Country]) extends Phase {
  def setSource(country:Country) = copy(source = Some(country))
}

case class Battle(source:Country, target:Country, previousBattle:Option[BattleResult] = None, isTransferring:Boolean = false) extends Phase

case class BattleResult(offRolls:List[Int] = Nil, defRolls:List[Int] = Nil, rGen: RandomFactory){
  import Scalaz._

  private val _offRolls: ListBuffer[Int] = ListBuffer[Int]()
  private val _defRolls: ListBuffer[Int] = ListBuffer[Int]()

  def attack(offensiveArmies:Int, defensiveArmies:Int):BattleResult = {
    for(a<-0 until offensiveArmies){
      _offRolls.append(rGen.roll())
    }
    for(a<-0 until defensiveArmies){
      _defRolls.append(rGen.roll())
    }

    copy(offRolls = _offRolls.toList, defRolls = _defRolls.toList)
  }

  def offDefArmiesLost(): (Int,Int) = {
    val sortedOffRolls = offRolls.sorted.reverse
    val sortedDefRolls = defRolls.sorted.reverse
    var offArmiesLost = 0
    var defArmiesLost = 0
    for(a<- sortedDefRolls.indices){
      (sortedDefRolls(a) >= sortedOffRolls(a)) ? (offArmiesLost += 1) | (defArmiesLost += 1)
    }

    (offArmiesLost, defArmiesLost)
  }
}

case class Reinforcement(source:Option[Country], target:Option[Country]) extends Phase



object InitPlacePhase{
  import WorldMap._

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

object TurnPlacePhase{
  import WorldMap._

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
    wm.copy(phase = Attacking(None))
  }
}

object AttackingPhase {

  def selectSource(wm:WorldMap, country:Country):WorldMap = {
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

object BattlePhase{

  def calculateDefensiveArmies(offArmies:Int, defensiveCountry: Country) = {
    if(offArmies == 1)
      1
    else if (defensiveCountry.armies == 1)
      1
    else 2
  }


  def recordResults(wm:WorldMap, battleResult: BattleResult): WorldMap = {
    wm.copy(phase = wm.phase.asInstanceOf[Battle].copy(previousBattle = Some(battleResult)))
  }

  def removeDeadArmies(wm:WorldMap): WorldMap = {
    val battle = wm.phase.asInstanceOf[Battle]
    val newSourceCountry = battle.source.removeArmies(battle.previousBattle.get.offDefArmiesLost()._1)
    val newTargetCountry = battle.target.removeArmies(battle.previousBattle.get.offDefArmiesLost()._2)
    val newBattle = battle.copy(source = newSourceCountry, target = newTargetCountry)

    wm.copy(phase = newBattle).updateSomeCountries(List(newSourceCountry, newTargetCountry))
  }

  def handleIfCountryConquered(wm:WorldMap):WorldMap = {
    val battle = wm.phase.asInstanceOf[Battle]
    if(battle.target.armies == 0){
      val conqueredCountry = battle.target.copy(owner = wm.getActivePlayer)
      wm.setPhase(battle.copy(target = conqueredCountry, isTransferring = true)).updateSingleCountry(conqueredCountry)
    }
    else wm
  }
}


object ReinforcementPhase {


  def setReinformentSource(wm: WorldMap, country: Country):WorldMap = wm.copy(phase = wm.phase.asInstanceOf[Reinforcement].copy(source = Some(country)))

  def setReinformentTarget(wm: WorldMap, country: Country):WorldMap = wm.copy(phase = wm.phase.asInstanceOf[Reinforcement].copy(target = Some(country)))

  def isValidSource(wm:WorldMap, country:Country):Boolean = {
    val phase = wm.phase.asInstanceOf[Reinforcement]
    phase.source.isEmpty && country.owner == wm.getActivePlayer
  }

  def isValidTarget(wm: WorldMap, country: Country): Boolean = {
    val phase = wm.phase.asInstanceOf[Reinforcement]
    if(phase.source.isEmpty)
      false
    else
      phase.source.map(_.adjacentCountries).get.contains(country.name) && country.owner == wm.getActivePlayer
  }

  def nextTurn(wm:WorldMap) :WorldMap = {
    val wm2 = wm.setNextActivePlayer().setPhase(TurnPlacement)
    TurnPlacePhase.beginTurn(wm2)
  }
}




case class WorldMap(countries: List[Country], players: List[Player], activePlayerNumber: Int, phase: Phase) {

  def setNextActivePlayer(): WorldMap = {
    if(activePlayerNumber == players.size)
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

  def getCountry(name:String): Country = countries.find(_.name == name).getOrElse(throw new Exception)

  def setPhase(phase: Phase): WorldMap = copy(phase = phase)

  def setActivePlayer(playerNumber: Int): WorldMap = copy(activePlayerNumber = playerNumber)

  def updateSingleCountry(country: Country): WorldMap = {
    copy(countries = countries.map(c => (c.name == country.name) ? country | c))
  }

  def transferArmies(source:Country, target:Country, amount:Int):WorldMap = updateSomeCountries(List(source.removeArmies(amount), target.addArmies(amount)))

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



