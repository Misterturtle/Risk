package Service

import javafx.scene.paint.{Paint, Color}

import GUI.Scaleable

import scalafx.beans.property.ReadOnlyObjectProperty
import scalafx.scene.Node


trait UIController extends Node with Scaleable

trait Input

case class CountryClicked(countryName:String) extends Input

case class TurnInCards(cards:List[Card]) extends Input

case class ConfirmBattle(source:Country, target:Country, offenseArmies:Int) extends Input

case class ConfirmTransfer(amount:Int) extends Input

case object CancelTransfer extends Input

case object EndAttackPhase extends Input

case object Retreat extends Input

case object EndTurn extends Input







trait CompInput

case object PlacementDelay extends CompInput

case object AttackSourceDelay extends CompInput

case object AttackTargetDelay extends CompInput


class ServiceInputHandler(wm: () => WorldMap, sideEffectManager: () => SideEffectManager) {

  def receiveInput(input:Input): Unit = {
    input match {
      case CountryClicked(countryName) =>
        sideEffectManager().performServiceEffect(Effects.getCountryClickedEffect(wm(), countryName))
      case TurnInCards(cards)=>
        sideEffectManager().performServiceEffect(Effects.turnInCards(wm(), cards))
      case ConfirmBattle(source,target,offenseArmies) =>
        sideEffectManager().performServiceEffect(Effects.executeBattle(wm(), ConfirmBattle(source, target, offenseArmies)))
       case ConfirmTransfer(amount) =>
        sideEffectManager().performServiceEffect(Effects.executeTransfer(wm(), ConfirmTransfer(amount)))
      case CancelTransfer =>
        sideEffectManager().performServiceEffect(Effects.cancelReinforcementTransfer(wm()))
      case Retreat =>
        sideEffectManager().performServiceEffect(Effects.retreatFromBattle(wm()))
      case EndAttackPhase =>
        sideEffectManager().performServiceEffect(Effects.endAttackPhase(wm()))
      case EndTurn =>
        sideEffectManager().performServiceEffect(Effects.endTurn(wm()))
    }
  }

  def receiveComputerDelay(input: CompInput): Unit = {
    input match{
      case PlacementDelay =>
        val (effect, future) = Effects.compPlacement(wm())
        sideEffectManager().performServiceEffect(effect)
        future.map(ci => this.receiveComputerDelay(ci))
      case AttackSourceDelay =>
        val (effect, future) = Effects.compAttackSource(wm())
        sideEffectManager().performServiceEffect(effect)
        future.map(ci => this.receiveComputerDelay(ci))
    }
  }
}
