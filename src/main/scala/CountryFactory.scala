/**
  * Created by Harambe on 7/13/2017.
  */
object CountryFactory {


  //North America
  val alaska = "alaska"
  val nwTerritory = "nwTerritory"
  val greenland = "greenland"
  val alberta = "alberta"
  val ontario = "ontario"
  val quebec = "quebec"
  val westernUS = "westernUS"
  val easternUS = "easternUS"
  val centralAmerica = "centralAmerica"




  //South America
  val venezuela = "venezuela"
  val peru = "peru"
  val brazil = "brazil"
  val argentina = "argentina"




  //Africa
  val northAfrica = "northAfrica"
  val congo = "congo"
  val southAfrica = "southAfrica"
  val madagascar = "madagascar"
  val eastAfrica = "eastAfrica"
  val egypt = "egypt"



  //Europe
  val iceland = "iceland"
  val britain = "britain"
  val westEurope = "westEurope"
  val northEurope = "northEurope"
  val southEurope = "southEurope"
  val scandinavia = "scandinavia"
  val ukraine = "ukraine"




  //Asia
  val ural = "ural"
  val kazakhstan = "kazakhstan"
  val middleEast = "middleEast"
  val india = "india"
  val siam = "siam"
  val china = "china"
  val mongolia = "mongolia"
  val irkutsk = "irkutsk"
  val siberia = "siberia"
  val yakutsk = "yakutsk"
  val kamchatka = "kamchatka"
  val japan = "japan"



  //Australia
  val indonesia = "indonesia"
  val papuaNewGuinea = "papuaNewGuinea"
  val westernAustralia = "westernAustralia"
  val easternAustralia = "easternAustralia"





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
    "northAmerica" -> northAmerica.map(_.name),
    "southAmerica" -> southAmerica.map(_.name),
    "africa" -> africa.map(_.name),
    "europe" -> europe.map(_.name),
    "asia" -> asia.map(_.name),
    "australia" -> australia.map(_.name)
  )

  implicit def blankCountry(name:String): Country = Country(name, 0, None, adjacentCountriesLookup(name))

}