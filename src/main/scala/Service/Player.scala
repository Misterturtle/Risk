package Service

import javafx.scene.paint.Paint

/**
  * Created by Harambe on 7/10/2017.
  */

object Players {
  def updatePlayers(worldMap: WorldMap)(players: List[Player]): WorldMap = {
    worldMap.copy(players = players)
  }
}

object Player {
  def removeArmies(playerId: Int, amount: Int): Action[List[Player]] = FlatAction { players: List[Player] =>
    val newArmyAmount = players
      .find(player => player.playerNumber == playerId)
      .map(player => player.armies - amount)
      .getOrElse(0)

    setArmiesForPlayer(playerId, newArmyAmount)
  }


  //todo: Set compiler to complain about match statement not being exhaustive

  def setArmiesForPlayer(playerId: Int, armiesAmount: Int): Action[List[Player]] = Action { players: List[Player] =>
    players.map { player =>
      if (player.playerNumber == playerId) {
        player.copy(armies = armiesAmount)
      } else {
        player
      }
    }
  }


//  def removeAllArmies(playerNumber: Int): Effect[Player] = Effect { player: Player =>
//    player match {
//      case HumanPlayer(name, `playerNumber`, _, color) => HumanPlayer(name, playerNumber, 0, color)
//      case ComputerPlayer(name, `playerNumber`, _, color) => ComputerPlayer(name, playerNumber, 0, color)
//      case _ => throw new Exception()
//    }
//  }
}

trait Player {
  val name: String
  val playerNumber: Int
  val armies: Int
  val color: Paint

  def copy(name: String = name, playerNumber: Int = playerNumber, armies: Int = armies, color: Paint = color): Player = {
    this match {
      case _: HumanPlayer => HumanPlayer(name, playerNumber, armies, color)
      case _: ComputerPlayer => ComputerPlayer(name, playerNumber, armies, color)
    }
  }

  def addArmies(amount: Int): Player = {
    this match {
      case h: HumanPlayer => h.copy(armies = armies + amount)
      case c: ComputerPlayer => c.copy(armies = armies + amount)
    }
  }

  def removeArmies(amount: Int): Player = {
    this match {
      case h: HumanPlayer => h.copy(armies = armies - amount)
      case c: ComputerPlayer => c.copy(armies = armies - amount)
    }
  }
}

case class ComputerPlayer(name: String, playerNumber: Int, armies: Int, color: Paint) extends Player

case class HumanPlayer(name: String, playerNumber: Int, armies: Int, color: Paint) extends Player