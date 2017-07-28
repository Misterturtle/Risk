package Service

import javafx.scene.paint.{Paint, Color}

/**
  * Created by Harambe on 7/10/2017.
  */

trait Player{
  val playerNumber:Int
  val armies:Int
  val color: Paint
  val name: String

  def addArmies(amount:Int): Player = {
    this match {
      case h: HumanPlayer => h.copy(armies = armies + amount)
      case c: ComputerPlayer => c.copy(armies = armies + amount)
    }
  }

  def removeArmies(amount:Int): Player = {
    this match {
      case h: HumanPlayer => h.copy(armies = armies - amount)
      case c: ComputerPlayer => c.copy(armies = armies - amount)
    }
  }
}

case class ComputerPlayer(name: String, playerNumber:Int, armies:Int, color:Paint) extends Player

case class HumanPlayer(name:String, playerNumber:Int, armies:Int, color:Paint) extends Player