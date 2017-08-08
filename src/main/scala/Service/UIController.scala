package Service

import javafx.scene.paint.{Paint, Color}

/**
  * Created by Harambe on 7/20/2017.
  */
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


class WorldMapUIController(val get:()=>WorldMap, sideEffectManager: SideEffectManager) extends UIController[WorldMap] {

  def receiveInput(input:Input): Unit = {
    input match {
      case CountryClicked(country) =>
        sideEffectManager.performServiceEffect(Effects.getCountryClickedEffect(get(), country))
      case ConfirmBattle(source,target,offenseArmies) =>
        sideEffectManager.performServiceEffect(Effects.executeBattle(get(), ConfirmBattle(source, target, offenseArmies)))
       case ConfirmTransfer(amount) =>
        sideEffectManager.performServiceEffect(Effects.executeBattleTransfer(get(), ConfirmTransfer(amount)))
      case Retreat =>
        sideEffectManager.performServiceEffect(Effects.retreatFromBattle(get()))
      case EndAttackPhase =>
        sideEffectManager.performServiceEffect(Effects.endAttackPhase(get()))
    }
  }


  def getCurrPlayersName: String = get().getActivePlayer.map(_.name).getOrElse("Invalid Player")

  def getCurrPlayersArmies: Int = get().getActivePlayer.map(_.armies).getOrElse(-1)

  def getCurrPlayersColor: Paint = get().getActivePlayer.map(_.color).getOrElse(Color.TRANSPARENT)

  def getCurrPlayersTerritories: Int = get().countries.count(_.owner.map(_.name).getOrElse("No Owner") == get().getActivePlayer.map(_.name).getOrElse("No Active Player"))

  def getCountries: List[Country] = get().countries

  def getCountryByName(name:String) = get().getCountry(name)

  def getPhase: Phase = get().phase



}
