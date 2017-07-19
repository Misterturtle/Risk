import scalafx.scene.paint.Color

/**
  * Created by Harambe on 7/10/2017.
  */

trait Player{
  val playerNumber:Int
  val armies:Int
  val color: String
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

case class HumanPlayer(name:String, playerNumber:Int, armies:Int, color:String) extends Player

case class ComputerPlayer(name: String, playerNumber:Int, armies:Int, color:String) extends Player
