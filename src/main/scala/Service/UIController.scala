package Service

import javafx.scene.paint.{Paint, Color}

import GUI.Scaleable

import scalafx.beans.property.ReadOnlyObjectProperty
import scalafx.scene.Node


trait UIController extends Node with Scaleable

trait Input

case class ConfirmBattle(source:Country, target:Country, offenseArmies:Int) extends Input

case class ConfirmTransfer(amount:Int) extends Input

case object CancelTransfer extends Input

case class CountryClicked(countryName:String) extends Input

case object EndAttackPhase extends Input

case object Retreat extends Input

case object EndTurn extends Input




class WorldMapUIController(wm: ReadOnlyObjectProperty[WorldMap], sideEffectManager: SideEffectManager) {

  def receiveInput(input:Input): Unit = {
    input match {
      case CountryClicked(countryName) =>
        sideEffectManager.performServiceEffect(Effects.getCountryClickedEffect(wm.value, countryName))
      case ConfirmBattle(source,target,offenseArmies) =>
        sideEffectManager.performServiceEffect(Effects.executeBattle(wm.value, ConfirmBattle(source, target, offenseArmies)))
       case ConfirmTransfer(amount) =>
        sideEffectManager.performServiceEffect(Effects.executeTransfer(wm.value, ConfirmTransfer(amount)))
      case CancelTransfer =>
        sideEffectManager.performServiceEffect(Effects.cancelReinforcementTransfer(wm.value))
      case Retreat =>
        sideEffectManager.performServiceEffect(Effects.retreatFromBattle(wm.value))
      case EndAttackPhase =>
        sideEffectManager.performServiceEffect(Effects.endAttackPhase(wm.value))
      case EndTurn =>
        sideEffectManager.performServiceEffect(Effects.endTurn(wm.value))
    }
  }


  def getCurrPlayersName: String = wm.value.getActivePlayer.map(_.name).getOrElse("Invalid Player")

  def getCurrPlayersArmies: Int = wm.value.getActivePlayer.map(_.armies).getOrElse(-1)

  def getCurrPlayersColor: Paint = wm.value.getActivePlayer.map(_.color).getOrElse(Color.TRANSPARENT)

  def getCurrPlayersTerritories: Int = wm.value.countries.count(_.owner.map(_.name).getOrElse("No Owner") == wm.value.getActivePlayer.map(_.name).getOrElse("No Active Player"))

  def getCountries: List[Country] = wm.value.countries

  def getCountryByName(name:String) = wm.value.getCountry(name)

  def getPhase: Phase = wm.value.phase



}
