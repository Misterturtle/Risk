/**
  * Created by Harambe on 7/10/2017.
  */

trait Player{
  val playerNumber:Int
  val armies:Int

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

case class HumanPlayer(playerNumber:Int, armies:Int) extends Player

case class ComputerPlayer(playerNumber:Int, armies:Int) extends Player
