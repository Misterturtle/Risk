package Service

/**
  * Created by Harambe on 7/13/2017.
  */
object CountryFactory {

  //North America
  val ALASKA = "Alaska"
  val NW_TERRITORY = "NW Territory"
  val GREENLAND = "Greenland"
  val ALBERTA = "Alberta"
  val ONTARIO = "Ontario"
  val QUEBEC = "Quebec"
  val WESTERN_US = "Western US"
  val EASTERN_US = "Eastern US"
  val CENTRAL_AMERICA = "Central America"

  //South America
  val VENEZUELA = "Venezuela"
  val PERU = "Peru"
  val BRAZIL = "Brazil"
  val ARGENTINA = "Argentina"

  //Africa
  val NORTH_AFRICA = "North Africa"
  val CONGO = "Congo"
  val SOUTH_AFRICA = "South Africa"
  val MADAGASCAR = "Madagascar"
  val EAST_AFRICA = "East Africa"
  val EGYPT = "Egypt"

  //Europe
  val ICELAND = "Iceland"
  val BRITAIN = "Britain"
  val WEST_EUROPE = "West Europe"
  val NORTH_EUROPE = "North Europe"
  val SOUTH_EUROPE = "South Europe"
  val SCANDINAVIA = "Scandinavia"
  val UKRAINE = "Ukraine"

  //Asia
  val URAL = "Ural"
  val KAZAKHSTAN = "Kazakhstan"
  val MIDDLE_EAST = "Middle East"
  val INDIA = "India"
  val SIAM = "Siam"
  val CHINA = "China"
  val MONGOLIA = "Mongolia"
  val IRKUTSK = "Irkutsk"
  val SIBERIA = "Siberia"
  val YAKUTSK = "Yakutsk"
  val KAMCHATKA = "Kamchatka"
  val JAPAN = "Japan"

  //Australia
  val INDONESIA = "Indonesia"
  val PAPUA_NEW_GUINEA = "Papua New Guinea"
  val WESTERN_AUSTRALIA = "Western Australia"
  val EASTERN_AUSTRALIA = "Eastern Australia"

  //Continents
  val NORTH_AMERICA = "North America"
  val SOUTH_AMERICA = "South America"
  val AFRICA = "Africa"
  val EUROPE = "Europe"
  val ASIA = "Asia"
  val AUSTRALIA = "Australia"


  lazy val adjacentCountriesLookup = Map[String, List[String]](
    ALASKA -> List(NW_TERRITORY, ALBERTA, KAMCHATKA),
    NW_TERRITORY -> List(ALASKA, GREENLAND, ALBERTA, ONTARIO),
    GREENLAND -> List(QUEBEC, NW_TERRITORY, ICELAND),
    ALBERTA -> List(ALASKA, ONTARIO, NW_TERRITORY, WESTERN_US),
    ONTARIO -> List(NW_TERRITORY, ALBERTA, WESTERN_US, EASTERN_US, QUEBEC),
    QUEBEC -> List(GREENLAND, EASTERN_US, ONTARIO),
    WESTERN_US -> List(CENTRAL_AMERICA, EASTERN_US, ONTARIO, ALBERTA),
    EASTERN_US -> List(QUEBEC, ONTARIO, ALBERTA, WESTERN_US, CENTRAL_AMERICA),
    CENTRAL_AMERICA -> List(WESTERN_US, EASTERN_US, ARGENTINA),
    VENEZUELA -> List(PERU, BRAZIL),
    PERU -> List(ARGENTINA, VENEZUELA, BRAZIL),
    BRAZIL -> List(PERU, ARGENTINA, VENEZUELA, NORTH_AFRICA),
    ARGENTINA -> List(PERU, BRAZIL, CENTRAL_AMERICA),
    NORTH_AFRICA -> List(WEST_EUROPE, SOUTH_EUROPE, EGYPT, EAST_AFRICA, CONGO, BRAZIL),
    CONGO -> List(NORTH_AFRICA, EAST_AFRICA, SOUTH_AFRICA),
    SOUTH_AFRICA -> List(MADAGASCAR, CONGO, EAST_AFRICA),
    MADAGASCAR -> List(SOUTH_AFRICA, EAST_AFRICA),
    EAST_AFRICA -> List(MADAGASCAR, SOUTH_AFRICA, CONGO, NORTH_AFRICA, EGYPT, MIDDLE_EAST),
    EGYPT -> List(SOUTH_EUROPE, MIDDLE_EAST, EAST_AFRICA, NORTH_AFRICA),
    //Europe
    ICELAND -> List(GREENLAND, SCANDINAVIA, BRITAIN),
    BRITAIN -> List(ICELAND, SCANDINAVIA, WEST_EUROPE, NORTH_EUROPE),
    WEST_EUROPE -> List(BRITAIN, NORTH_AFRICA, SOUTH_EUROPE, NORTH_EUROPE),
    NORTH_EUROPE -> List(BRITAIN, WEST_EUROPE, SOUTH_EUROPE, UKRAINE, SCANDINAVIA),
    SOUTH_EUROPE -> List(NORTH_AFRICA, WEST_EUROPE, EGYPT, UKRAINE, NORTH_EUROPE),
    SCANDINAVIA -> List(UKRAINE, NORTH_EUROPE, BRITAIN, ICELAND),
    UKRAINE -> List(SCANDINAVIA, NORTH_EUROPE, SOUTH_EUROPE, MIDDLE_EAST, KAZAKHSTAN, URAL),
    //Asia
    URAL -> List(SIBERIA, CHINA, KAZAKHSTAN, UKRAINE),
    KAZAKHSTAN -> List(URAL, UKRAINE, MIDDLE_EAST, INDIA, CHINA),
    MIDDLE_EAST -> List(INDIA, KAZAKHSTAN, UKRAINE, SOUTH_EUROPE, EGYPT, EAST_AFRICA),
    INDIA -> List(MIDDLE_EAST, KAZAKHSTAN, CHINA, SIAM),
    SIAM -> List(INDIA, CHINA, INDONESIA),
    CHINA -> List(SIAM, INDIA, KAZAKHSTAN, URAL, SIBERIA, MONGOLIA),
    MONGOLIA -> List(CHINA, JAPAN, SIBERIA, IRKUTSK, KAMCHATKA),
    IRKUTSK -> List(YAKUTSK, KAMCHATKA, MONGOLIA, SIBERIA),
    SIBERIA -> List(YAKUTSK, IRKUTSK, MONGOLIA, CHINA, URAL),
    YAKUTSK -> List(KAMCHATKA, IRKUTSK, SIBERIA),
    KAMCHATKA -> List(YAKUTSK, IRKUTSK, MONGOLIA, ALASKA),
    JAPAN -> List(MONGOLIA, KAMCHATKA),
    INDONESIA -> List(SIAM, WESTERN_AUSTRALIA, PAPUA_NEW_GUINEA),
    WESTERN_AUSTRALIA -> List(EASTERN_AUSTRALIA, INDONESIA, PAPUA_NEW_GUINEA),
    EASTERN_AUSTRALIA -> List(WESTERN_AUSTRALIA, PAPUA_NEW_GUINEA),
    PAPUA_NEW_GUINEA -> List(INDONESIA, WESTERN_AUSTRALIA, EASTERN_AUSTRALIA)
  )

  val northAmerica = List[Country](ALASKA, NW_TERRITORY, GREENLAND, ALBERTA, ONTARIO, QUEBEC, WESTERN_US,
    EASTERN_US, CENTRAL_AMERICA)
  val africa = List[Country](NORTH_AFRICA, CONGO, SOUTH_AFRICA, MADAGASCAR, EAST_AFRICA, EGYPT)
  val asia = List[Country](URAL, KAZAKHSTAN, MIDDLE_EAST, INDIA, SIAM, CHINA, MONGOLIA, IRKUTSK, SIBERIA, YAKUTSK, KAMCHATKA, JAPAN)
  val australia = List[Country](INDONESIA, PAPUA_NEW_GUINEA, WESTERN_AUSTRALIA, EASTERN_AUSTRALIA)
  val europe = List[Country](ICELAND, BRITAIN, WEST_EUROPE, NORTH_EUROPE, SOUTH_EUROPE, SCANDINAVIA, UKRAINE)
  val southAmerica = List[Country](VENEZUELA, PERU, BRAZIL, ARGENTINA)



  def getCountries: List[Country] = {
    northAmerica ++ southAmerica ++ africa ++ asia ++ europe ++ australia
  }


  def continentLookup: Map[String, List[String]] = Map[String, List[String]](
    NORTH_AMERICA -> northAmerica.map(_.name),
    SOUTH_AMERICA -> southAmerica.map(_.name),
    AFRICA -> africa.map(_.name),
    EUROPE -> europe.map(_.name),
    ASIA -> asia.map(_.name),
    AUSTRALIA -> australia.map(_.name)
  )

  implicit def blankCountry(name:String): Country = Country(name, 0, None, adjacentCountriesLookup(name))
}