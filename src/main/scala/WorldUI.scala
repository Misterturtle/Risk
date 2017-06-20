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
class WorldUI extends AnchorPane {

  this.stylesheets.add("worldStyle.css")
  this.styleClass.add("test")

  val origImage = new Image("map.jpg")
  val origX = origImage.width.value
  val origY = origImage.height.value

  val bgXScale = new SimpleDoubleProperty()
  val bgYScale = new SimpleDoubleProperty()
  bgXScale.bind(this.widthProperty().divide(origX))
  bgYScale.bind(this.heightProperty().divide(origY))



  val northAmerica = new Country("alaska", CountryPixelDB.alaska)
  val nwTerritory = new Country("nwTerritory", CountryPixelDB.nwTerritory)
  val greenland = new Country("greenland", CountryPixelDB.greenLand)
  val alberta = new Country("alberta", CountryPixelDB.alberta)
  val ontario = new Country("ontario", CountryPixelDB.ontario)
  val quebec = new Country("quebec", CountryPixelDB.quebec)
  val westernUS = new Country("westernUS", CountryPixelDB.westernUS)
  val easternUS = new Country("easternUS", CountryPixelDB.easternUS)
  val centralAmerica = new Country("centralAmerica", CountryPixelDB.centralAmerica)


  val countries = List[Country](northAmerica, nwTerritory, greenland, alberta, ontario, quebec, westernUS, easternUS, centralAmerica)

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
    }
  }

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

  bindCountries()
  countries.foreach{x=> this.children.add(x)}
  countries.foreach{x=> x.setFill(listOfColors(Random.nextInt(9)))}




  this.setOnMouseClicked(new EventHandler[MouseEvent] {
    override def handle(event: MouseEvent): Unit =
    {
      println("("+event.getX * (1/bgXScale.get())+","+event.getY * (1/bgYScale.get())+"),")
    }
  })



}
