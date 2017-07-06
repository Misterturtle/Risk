/**
  * Created by Harambe on 6/18/2017.
  */

trait Player{
  private var _availableArmies = 0
  def availableArmies: Int = _availableArmies
  private var _playerNumber = 0
  def playerNumber = _playerNumber

  def addAvailableArmies(amount:Int):Unit = _availableArmies += amount
  def removeAvailableArmies(amount:Int):Unit = _availableArmies -= amount
}

class HumanPlayer() extends Player


class ComputerPlayer() extends Player



