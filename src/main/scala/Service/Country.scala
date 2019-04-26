package Service

case object Country {
  def addArmies(countryName: String, amount: Int): Action[List[Country]] = Action { countries: List[Country] =>
    countries.map { country =>
      if (country.name == countryName) {
        country.copy(armies = country.armies + amount)
      } else {
        country
      }
    }
  }

  def setOwner(countryId: String, player: Player): Action[List[Country]] = Action { countries: List[Country] =>
    countries.map(country => {
      if (country.name == countryId) {
        country.copy(owner = Some(player))
      } else {
        country
      }
    })
  }

  val EMPTY = new Country("", 0, None, Nil)

  def update(): Unit = {

  }
}

case class Country(name: String, armies: Int, owner: Option[Player], adjacentCountries: List[String]) {
  def removeArmies(amount: Int): Country = copy(armies = armies - amount)

  def addArmies(amount: Int): Country = copy(armies = armies + amount)

  def setOwner(player: Player): Country = copy(owner = Some(player))

  def isOwnedBy(playerNumber: Int): Boolean = owner.map(_.playerNumber).contains(playerNumber)
}


