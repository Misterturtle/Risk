///**
//  * Created by Harambe on 7/9/2017.
//  */
//
//case class Armies(armies:Int){
//
//  def addArmies(amount:Int): Armies = copy(armies + amount)
//  def removeArmies(amount:Int): Armies = copy(armies - amount)
//}
//
//trait Player{
//  val playerNumber:Int
//  val armies:Armies
//  def addArmies(amount:Int):Player
//  def removeArmies(amount:Int): Player
//}
//
//case class HumanPlayer(playerNumber:Int, armies:Armies) extends Player{
//  override def addArmies(amount: Int): Player = copy(armies = armies.addArmies(amount))
//  override def removeArmies(amount: Int): Player = copy(armies = armies.removeArmies(amount))
//}
//
//case class Country(owner:Option[Player], armies:Armies){
//  def addArmies(amount:Int):WorldMap = copy(armies = armies.addArmies(amount))
//  def removeArmies(amount:Int):WorldMap = copy(armies = armies.removeArmies(amount))
//  def changeOwner(owner:Player):WorldMap = copy(owner = Some(owner))
//  def isOwned:Boolean = owner.nonEmpty
//}
//
//case class PlayerController(players: List[Player]){
//  def updatePlayer(player:Player) = (Player) => Player
//}
//
//case class TurnController(players: ()=>List[Player], activePlayer:Option[Player], lastActivePlayerNumber:Int){
//  def endTurn():TurnController = copy(activePlayer = None, lastActivePlayerNumber = activePlayer.map(_.playerNumber).getOrElse(0))
//  def beginNextTurn():TurnController = copy(activePlayer = Some(players().find(_.playerNumber == lastActivePlayerNumber + 1).getOrElse(players().head)))
//}
//
//trait Phase
//case object InitialPlacement extends Phase
//case object TurnPlacement extends Phase
//case object AttackPhase extends Phase
//case object TransferPhase
//
//trait CountryState
//case object Owned extends CountryState
//
//
//
//
//
//
//
//object Methods {
//
//  // Add army to country                (armies:Int, country:Country) => Country
//  // Remove army from player            (armies:Int, player:Player) => Player
//  // Set countrys on click              (action:()=>Unit, country:Country) => Country
//  // Assign player a player number      (playerNumber:Int, player:Player) => Player
//  // Calculate Initial Armies              ((players:List[Player]) => Int) => Player
//
//
//
//  def addArmyToCountry(armies:Int, country: WorldMap): WorldMap = ???
//  def removeArmyFromCountry(armies:Int, country:WorldMap):WorldMap = ???
//  def assignPlayerNumber(number:Int, player:Int) = ???
//  def addArmy(amount:Int, entity: HasArmies) =>
//
//
//
//
//
//  //Side Effects
//  def setCountryOnClickAction(action:()=>Unit, country:WorldMap): WorldMap = ???
//
//
//
//
//
//}
