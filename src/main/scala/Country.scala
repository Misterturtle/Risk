import javafx.event.EventHandler
import javafx.scene.input.MouseEvent

import scalafx.scene.layout.Pane
import scalaz._
import scalaz.Scalaz._


sealed trait Interaction

case object Placeable extends Interaction
case object Selectable extends Interaction
case object Targetable extends Interaction
case object NoInteraction extends Interaction

case class Country(name: String, armies: Int, owner: Option[Player], GUI: Pane){

  def setOnClick(action: ()=>Unit): Country ={
    GUI.setOnMouseClicked(new EventHandler[MouseEvent] {
      override def handle(event: MouseEvent): Unit = action()
    })
      copy()
  }


  def removeArmies(amount:Int): Country = copy(armies = armies - amount)
  def addArmies(amount:Int):Country = copy(armies = armies + amount)
  def setOwner(player:Player):Country = copy(owner = Some(player))

}


