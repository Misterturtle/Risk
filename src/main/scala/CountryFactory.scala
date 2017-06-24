/**
  * Created by Harambe on 6/20/2017.
  */
class CountryFactory {

  def newCountry(name: String, origPoints: List[(Double, Double)]) : Country = {
    new Country(name, origPoints)
  }



}
