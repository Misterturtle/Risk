import TypeAlias.Effect

import scalaz._
import Scalaz._
/**
  * Created by Harambe on 7/17/2017.
  */
trait Input
case class CountryClicked(country:Country) extends Input
case class ConfirmBattle(source:Country, target:Country, armiesAttacking:Int) extends Input

trait InputHandler[A] {
  def receiveInput(input:Input): Unit
  val get: () => A
}

class WorldMapInputHandler(val get:()=>WorldMap, sideEffectManager: SideEffectManager) extends InputHandler[WorldMap] {

  def receiveInput(input:Input): Unit = {
    input match {
      case CountryClicked(country) =>
        sideEffectManager.performEffect(Effects.getCountryClickedEffect(get(), country))
      case ConfirmBattle(source:Country, target:Country, armiesAttacking:Int) =>


    }
  }





}
