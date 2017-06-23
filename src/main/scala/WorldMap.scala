import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.event.EventHandler
import javafx.scene.input.MouseEvent

import scala.util.Random
import scalafx.scene.image.Image
import scalafx.scene.layout.AnchorPane
import scalafx.scene.paint.Color


/**
  * Created by Harambe on 6/16/2017.
  */

class WorldMap(countryFactory: CountryFactory) extends AnchorPane {

  val bgXScale = new SimpleDoubleProperty()
  val bgYScale = new SimpleDoubleProperty()

  protected var _initialPlacementComplete = false
  val baseState = WorldMapState(_initialPlacementComplete _)


  val northAmerica = countryFactory.newCountry("alaska", CountryPixelDB.alaska)
  val nwTerritory = countryFactory.newCountry("nwTerritory", CountryPixelDB.nwTerritory)
  val greenland = countryFactory.newCountry("greenland", CountryPixelDB.greenLand)
  val alberta = countryFactory.newCountry("alberta", CountryPixelDB.alberta)
  val ontario = countryFactory.newCountry("ontario", CountryPixelDB.ontario)
  val quebec = countryFactory.newCountry("quebec", CountryPixelDB.quebec)
  val westernUS = countryFactory.newCountry("westernUS", CountryPixelDB.westernUS)
  val easternUS = countryFactory.newCountry("easternUS", CountryPixelDB.easternUS)
  val centralAmerica = countryFactory.newCountry("centralAmerica", CountryPixelDB.centralAmerica)
  val countries = List[Country](northAmerica, nwTerritory, greenland, alberta, ontario, quebec, westernUS, easternUS, centralAmerica)

  def styleMap(): Unit = {
    this.stylesheets.add("worldStyle.css")
    this.styleClass.add("test")
  }


  def scaleMap(): Unit ={
    val origImage = new Image("map.jpg")
    val origX = origImage.width.value
    val origY = origImage.height.value
    bgXScale.bind(this.widthProperty().divide(origX))
    bgYScale.bind(this.heightProperty().divide(origY))
  }

  def bindCountries(): Unit ={
    countries.foreach{
      case country =>
        this.heightProperty().addListener(new ChangeListener[Number] {
          override def changed(observable: ObservableValue[_ <: Number], oldValue: Number, newValue: Number): Unit = {

            println("Changed")
            println("XScale: " + bgXScale.get())
            println("YScale: " + bgYScale.get())
            country.getPoints.removeAll(country.getPoints)
            country.origPoints.foreach { point =>
              country.getPoints.add(point._1 * bgXScale.get())
              country.getPoints.add(point._2 * bgYScale.get())
            }
          }
        })
        this.children.add(country)
    }
  }

  def  enableDebug(): Unit ={
    val listOfColors = List[Color](
      Color.Red,
      Color.Blue,
      Color.Green,
      Color.Beige,
      Color.Black,
      Color.White,
      Color.Yellow,
      Color.Orange,
      Color.Gold
    )

    this.setOnMouseClicked(new EventHandler[MouseEvent] {
      override def handle(event: MouseEvent): Unit =
      {
        println("("+event.getX * (1/bgXScale.get())+","+event.getY * (1/bgYScale.get())+"),")
      }
    })

    countries.foreach{x=> x.setFill(listOfColors(Random.nextInt(9)))}
  }

  def init(): Unit ={
    styleMap()
    scaleMap()
    bindCountries()

    //DEBUG
    enableDebug()
  }
}