/**
  * Created by Harambe on 7/13/2017.
  */
object CountryFactory {

  def blankCountry(name: String): Country = Country(name, 0, None, () => {})

  //North America
  val alaska = blankCountry("alaska")
  val nwTerritory = blankCountry("nwTerritory")
  val greenland = blankCountry("greenland")
  val alberta = blankCountry("alberta")
  val ontario = blankCountry("ontario")
  val quebec = blankCountry("quebec")
  val westernUS = blankCountry("westernUS")
  val easternUS = blankCountry("easternUS")
  val centralAmerica = blankCountry("centralAmerica")

  val northAmerica = List[Country](alaska, nwTerritory, greenland, alberta, ontario, quebec, westernUS,
    easternUS, centralAmerica)

  //South America
  val venezuela = blankCountry("venezuela")
  val peru = blankCountry("peru")
  val brazil = blankCountry("brazil")
  val argentina = blankCountry("argentina")

  val southAmerica = List[Country](venezuela, peru, brazil, argentina)

  //Africa
  val northAfrica = blankCountry("northAfrica")
  val congo = blankCountry("congo")
  val southAfrica = blankCountry("southAfrica")
  val madagascar = blankCountry("madagascar")
  val eastAfrica = blankCountry("eastAfrica")
  val egypt = blankCountry("egypt")

  val africa = List[Country](northAfrica, congo, southAfrica, madagascar, eastAfrica, egypt)


  //Europe
  val iceland = blankCountry("iceland")
  val britain = blankCountry("britain")
  val westEurope = blankCountry("westEurope")
  val northEurope = blankCountry("northEurope")
  val southEurope = blankCountry("southEurope")
  val scandinavia = blankCountry("scandinavia")
  val ukraine = blankCountry("ukraine")

  val europe = List[Country](iceland, britain, westEurope, northEurope, southEurope, scandinavia, ukraine)


  //Asia
  val ural = blankCountry("ural")
  val kazakhstan = blankCountry("kazakhstan")
  val middleEast = blankCountry("middleEast")
  val india = blankCountry("india")
  val siam = blankCountry("siam")
  val china = blankCountry("china")
  val mongolia = blankCountry("mongolia")
  val irkutsk = blankCountry("irkutsk")
  val siberia = blankCountry("siberia")
  val yakutsk = blankCountry("yakutsk")
  val kamchatka = blankCountry("kamchatka")
  val japan = blankCountry("japan")

  val asia = List[Country](ural, kazakhstan, middleEast, india, siam, china, mongolia, irkutsk, siberia, yakutsk, kamchatka, japan)


  //Australia
  val indonesia = blankCountry("indonesia")
  val papuaNewGuinea = blankCountry("papuaNewGuinea")
  val westernAustralia = blankCountry("westernAustralia")
  val easternAustralia = blankCountry("easternAustralia")

  val australia = List[Country](indonesia, papuaNewGuinea, westernAustralia, easternAustralia)


  def getBlankCountries: List[Country] = {
    northAmerica
  }


  def continentLookup: Map[String, List[String]] = Map[String, List[String]](
    "northAmerica" -> northAmerica.map(_.name),
    "southAmerica" -> southAmerica.map(_.name),
    "africa" -> africa.map(_.name),
    "europe" -> europe.map(_.name),
    "asia" -> asia.map(_.name),
    "australia" -> australia.map(_.name)
  )
}