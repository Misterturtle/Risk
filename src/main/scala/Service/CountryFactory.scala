package Service

/**
  * Created by Harambe on 7/13/2017.
  */
object CountryFactory {


  //North America
  val alaska = "Alaska"
  val nwTerritory = "NW Territory"
  val greenland = "Greenland"
  val alberta = "Alberta"
  val ontario = "Ontario"
  val quebec = "Quebec"
  val westernUS = "Western US"
  val easternUS = "Eastern US"
  val centralAmerica = "Central America"




  //South America
  val venezuela = "Venezuela"
  val peru = "Peru"
  val brazil = "Brazil"
  val argentina = "Argentina"




  //Africa
  val northAfrica = "North Africa"
  val congo = "Congo"
  val southAfrica = "South Africa"
  val madagascar = "Madagascar"
  val eastAfrica = "East Africa"
  val egypt = "Egypt"



  //Europe
  val iceland = "Iceland"
  val britain = "Britain"
  val westEurope = "West Europe"
  val northEurope = "North Europe"
  val southEurope = "South Europe"
  val scandinavia = "Scandinavia"
  val ukraine = "Ukraine"




  //Asia
  val ural = "Ural"
  val kazakhstan = "Kazakhstan"
  val middleEast = "Middle East"
  val india = "India"
  val siam = "Siam"
  val china = "China"
  val mongolia = "Mongolia"
  val irkutsk = "Irkutsk"
  val siberia = "Siberia"
  val yakutsk = "Yakutsk"
  val kamchatka = "Kamchatka"
  val japan = "Japan"



  //Australia
  val indonesia = "Indonesia"
  val papuaNewGuinea = "Papua New Guinea"
  val westernAustralia = "Western Australia"
  val easternAustralia = "Eastern Australia"





  lazy val adjacentCountriesLookup = Map[String, List[String]](
    alaska -> List(nwTerritory, alberta, kamchatka),
    nwTerritory -> List(alaska, greenland, alberta, ontario),
    greenland -> List(quebec, nwTerritory, iceland),
    alberta -> List(alaska, ontario, nwTerritory, westernUS),
    ontario -> List(nwTerritory, alberta, westernUS, easternUS, quebec),
    quebec -> List(greenland, easternUS, ontario),
    westernUS -> List(centralAmerica, easternUS, ontario, alberta),
    easternUS -> List(quebec, ontario, alberta, westernUS, centralAmerica),
    centralAmerica -> List(westernUS, easternUS, argentina),
    venezuela -> List(peru, brazil),
    peru -> List(argentina, venezuela, brazil),
    brazil -> List(peru, argentina, venezuela, northAfrica),
    argentina -> List(peru, brazil, centralAmerica),
    northAfrica -> List(westEurope, southEurope, egypt, eastAfrica, congo, brazil),
    congo -> List(northAfrica, eastAfrica, southAfrica),
    southAfrica -> List(madagascar, congo, eastAfrica),
    madagascar -> List(southAfrica, eastAfrica),
    eastAfrica -> List(madagascar, southAfrica, congo, northAfrica, egypt, middleEast),
    egypt -> List(southEurope, middleEast, eastAfrica, northAfrica),
    //Europe
    iceland -> List(greenland, scandinavia, britain),
    britain -> List(iceland, scandinavia, westEurope, northEurope),
    westEurope -> List(britain, northAfrica, southEurope, northEurope),
    northEurope -> List(britain, westEurope, southEurope, ukraine, scandinavia),
    southEurope -> List(northAfrica, westEurope, egypt, ukraine, northEurope),
    scandinavia -> List(ukraine, northEurope, britain, iceland),
    ukraine -> List(scandinavia, northEurope, southEurope, middleEast, kazakhstan, ural),
    //Asia
    ural -> List(siberia, china, kazakhstan, ukraine),
    kazakhstan -> List(ural, ukraine, middleEast, india, china),
    middleEast -> List(india, kazakhstan, ukraine, southEurope, egypt, eastAfrica),
    india -> List(middleEast, kazakhstan, china, siam),
    siam -> List(india, china, indonesia),
    china -> List(siam, india, kazakhstan, ural, siberia, mongolia),
    mongolia -> List(china, japan, siberia, irkutsk, kamchatka),
    irkutsk -> List(yakutsk, kamchatka, mongolia, siberia),
    siberia -> List(yakutsk, irkutsk, mongolia, china, ural),
    yakutsk -> List(kamchatka, irkutsk, siberia),
    kamchatka -> List(yakutsk, irkutsk, mongolia, alaska),
    japan -> List(mongolia, kamchatka),
    indonesia -> List(siam, westernAustralia, papuaNewGuinea),
    westernAustralia -> List(easternAustralia, indonesia, papuaNewGuinea),
    easternAustralia -> List(westernAustralia, papuaNewGuinea),
    papuaNewGuinea -> List(indonesia, westernAustralia, easternAustralia)
  )

  val northAmerica = List[Country](alaska, nwTerritory, greenland, alberta, ontario, quebec, westernUS,
    easternUS, centralAmerica)
  val africa = List[Country](northAfrica, congo, southAfrica, madagascar, eastAfrica, egypt)
  val asia = List[Country](ural, kazakhstan, middleEast, india, siam, china, mongolia, irkutsk, siberia, yakutsk, kamchatka, japan)
  val australia = List[Country](indonesia, papuaNewGuinea, westernAustralia, easternAustralia)
  val europe = List[Country](iceland, britain, westEurope, northEurope, southEurope, scandinavia, ukraine)
  val southAmerica = List[Country](venezuela, peru, brazil, argentina)



  def getCountries: List[Country] = {
    northAmerica ++ southAmerica ++ africa ++ asia ++ europe ++ australia
  }


  def continentLookup: Map[String, List[String]] = Map[String, List[String]](
    "North America" -> northAmerica.map(_.name),
    "South America" -> southAmerica.map(_.name),
    "Africa" -> africa.map(_.name),
    "Europe" -> europe.map(_.name),
    "Asia" -> asia.map(_.name),
    "Australia" -> australia.map(_.name)
  )

  implicit def blankCountry(name:String): Country = Country(name, 0, None, adjacentCountriesLookup(name))
}