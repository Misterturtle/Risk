package Service

import GUI.WorldMapUI
import javafx.scene.paint.{Color, Paint}

trait UIController[A] {
  def receiveInput(input:Input): Unit
  val get: () => A
}

trait Input

case class ConfirmBattle(source:Country, target:Country, offenseArmies:Int) extends Input

case class ConfirmTransfer(amount:Int) extends Input

case class CountryClicked(country:Country) extends Input

case object EndAttackPhase extends Input

case object Retreat extends Input


class WorldMapUIController(val get:()=>WorldMap) extends UIController[WorldMap] {

  val worldMapUI = new WorldMapUI(this)

  def receiveInput(input:Input): Unit = {
    input match {
      case CountryClicked(country) =>
        SideEffectManager.receive(Effects.getCountryClickedEffect(get(), country))
      case ConfirmBattle(source,target,offenseArmies) =>
        SideEffectManager.receive(Effects.executeBattle(get(), ConfirmBattle(source, target, offenseArmies)))
       case ConfirmTransfer(amount) =>
        SideEffectManager.receive(Effects.executeBattleTransfer(get(), ConfirmTransfer(amount)))
      case Retreat =>
        SideEffectManager.receive(Effects.retreatFromBattle(get()))
      case EndAttackPhase =>
        SideEffectManager.receive(Effects.endAttackPhase(get()))
    }
  }


  def updateWorldMap(worldMap: WorldMap): Unit = worldMapUI.updateWorldMap(worldMap)

  def getCurrPlayersName: String = get().getActivePlayer.map(_.name).getOrElse("Invalid Player")

  def getCurrPlayersArmies: Int = get().getActivePlayer.map(_.armies).getOrElse(-1)

  def getCurrPlayersColor: Paint = get().getActivePlayer.map(_.color).getOrElse(Color.TRANSPARENT)

  def getCurrPlayersTerritories: Int = get().countries.count(_.owner.map(_.name).getOrElse("No Owner") == get().getActivePlayer.map(_.name).getOrElse("No Active Player"))

  def getCountries: List[Country] = get().countries

  def getCountryByName(name:String) = get().getCountry(name)

  def getPhase: Phase = get().phase



}
