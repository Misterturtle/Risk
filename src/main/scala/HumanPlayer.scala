/**
  * Created by Harambe on 6/18/2017.
  */

trait Player{
  private var _availableArmies = 0
  def availableArmies: Int = _availableArmies
  private var _playerNumber = 0
  def playerNumber = _playerNumber
  def playerNumber_= (value:Int):Unit = _playerNumber = value

  def init(pNumber:Int): Unit


  def addAvailableArmies(amount:Int):Unit = _availableArmies += amount
  def removeAvailableArmies(amount:Int):Unit = _availableArmies -= amount
}

class HumanPlayer() extends Player{
  override def init(pNumber:Int): Unit = {
    playerNumber = pNumber
  }
}


class ComputerPlayer() extends Player{
  override  def init(pNumber:Int): Unit = {
    playerNumber = pNumber
  }
}



