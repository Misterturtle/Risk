package Service

import Service.TypeAlias.Effect
import javafx.scene.paint.{Color, Paint}

/**
  * Created by Harambe on 7/10/2017.
  */

object Player {

  //todo: Set compiler to complain about match statement not being exhaustive

  def setArmies(armiesAmount: Int): Player = Effect { player:Player =>
    player match {
      case h: HumanPlayer => h.copy(armies = 0)
      case c: ComputerPlayer => c.copy(armies = 0)
    }
  }


  def removeAllArmies(playerNumber:Int): Effect[Player] = Effect { player:Player =>
    player match {
      case h: HumanPlayer => h.copy(armies = 0)
      case c: ComputerPlayer => c.copy(armies = 0)
    }
  }
}

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