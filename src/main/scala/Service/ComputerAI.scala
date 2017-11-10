package Service

/**
  * Created by Harambe on 9/5/2017.
  */
case class WorldMapThreatAnalysis(countryName: String, wm:WorldMap){


  val continentAnalysis = List(


  )



  val continent = CountryFactory.country2Continent(countryName)
  val continentCountries = CountryFactory.continentLookup(continent)
  val isContinentOwned = continentCountries.forall(wm.getCountry(_).owner == wm.getActivePlayer)
  val countriesOwned = continentCountries.count(wm.getCountry(_).owner.get.playerNumber == wm.getActivePlayer.get.playerNumber)
  val countriesNotOwned = continentCountries.count(wm.getCountry(_).owner.get.playerNumber != wm.getActivePlayer.get.playerNumber)
  val playerCountries:Map[Int,List[String]] = wm.players.map(p => (p.playerNumber, continentCountries.filter(c => wm.getCountry(c).owner.get.playerNumber == p.playerNumber))).toMap
  val playerArmies = wm.players.map(p => (p.playerNumber, playerCountries(p.playerNumber).map(wm.getCountry(_).armies).sum)).toMap
  val playerArmyPercentage = playerArmies.map(t => t._1 -> t._2/playerArmies.values.sum)



  def borderCountries(continentName: String) = {
    import CountryFactory._
    val borderLookup = Map[String, List[String]](
      "North America" -> List(alaska, centralAmerica, greenland),
      "South America" -> List(venezuela, brazil),
      "Africa" -> List(northAfrica, egypt, eastAfrica),
      "Europe" -> List(westernEurope, iceland, ukraine),
      "Asia" -> List(siam, middleEast, afghanistan, ural, kamchatka),
      "Australia" -> List(indonesia)
    )
    borderLookup(continentName)
  }


}

object ComputerAI {

  def chooseAttackSource(wm:WorldMap): WorldMap = {
    val contientAnalysis = new ContinentThreatAnalysis()

  }

  private def secureOwnedContinents(wm:WorldMap): Unit ={

  }

}
