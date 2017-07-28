package Service


case class Country(name: String, armies: Int, owner: Option[Player], adjacentCountries: List[String]){
  def removeArmies(amount:Int): Country = copy(armies = armies - amount)
  def addArmies(amount:Int):Country = copy(armies = armies + amount)
  def setOwner(player:Player):Country = copy(owner = Some(player))
  def isOwnedBy(playerNumber: Int):Boolean = owner.map(_.playerNumber).contains(playerNumber)
}


