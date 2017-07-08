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

class WorldMap(countryFactory: CountryFactory, val players: List[Player]) extends AnchorPane {

  val bgXScale = new SimpleDoubleProperty()
  val bgYScale = new SimpleDoubleProperty()

  protected var _initialPlacementComplete = false


  val northAmerica = countryFactory.newCountry("alaska", CountryPixelDB.alaska)
  val nwTerritory = countryFactory.newCountry("nwTerritory", CountryPixelDB.nwTerritory)
  val greenland = countryFactory.newCountry("greenland", CountryPixelDB.greenLand)
  val alberta = countryFactory.newCountry("alberta", CountryPixelDB.alberta)
  val ontario = countryFactory.newCountry("ontario", CountryPixelDB.ontario)
  val quebec = countryFactory.newCountry("quebec", CountryPixelDB.quebec)
  val westernUS = countryFactory.newCountry("westernUS", CountryPixelDB.westernUS)
  val easternUS = countryFactory.newCountry("easternUS", CountryPixelDB.easternUS)
  val centralAmerica = countryFactory.newCountry("centralAmerica", CountryPixelDB.centralAmerica)
  val countries = Map[String, Country](
    northAmerica.name -> northAmerica, nwTerritory.name -> nwTerritory, greenland.name -> greenland,
    alberta.name -> alberta, ontario.name -> ontario, quebec.name -> quebec, westernUS.name -> westernUS,
    easternUS.name -> easternUS, centralAmerica.name -> centralAmerica)

  val baseState = WorldMapState(_initialPlacementComplete _, players, countries)


  def styleMap(): Unit = {
    this.stylesheets.add("worldStyle.css")
    this.styleClass.add("worldMap")
  }


  def scaleMap(): Unit = {
    val origImage = new Image("map.jpg")
    val origX = origImage.width.value
    val origY = origImage.height.value
    bgXScale.bind(width.delegate.divide(origX))
    bgYScale.bind(height.delegate.divide(origY))
  }



  def enableDebug(): Unit = {
    this.setOnMouseClicked(new EventHandler[MouseEvent] {
      override def handle(event: MouseEvent): Unit = {
        println("(" + event.getX * (1 / bgXScale.get()) + "," + event.getY * (1 / bgYScale.get()) + "),")
      }
    })

    //countries.foreach{_._2.drawDebug()}


  }

  def initCountries(): Unit = {
    countries.foreach {case (name, country) =>
      this.children.add(country)
      country.initShape(bgXScale, bgYScale)
      AnchorPane.setTopAnchor(country, country.origPoints.minBy(_._2)._2 * bgYScale.get())
      AnchorPane.setLeftAnchor(country, country.origPoints.minBy(_._1)._1 * bgXScale.get())
      country.resizePoly()
    }


    this.widthProperty().addListener(new ChangeListener[Number] {
      override def changed(observable: ObservableValue[_ <: Number], oldValue: Number, newValue: Number): Unit = {
        countries.foreach{case(name,country) =>
          AnchorPane.setLeftAnchor(country, country.origPoints.minBy(_._1)._1 * bgXScale.get())
          country.resizePoly()
        }
      }
    })

    this.heightProperty().addListener(new ChangeListener[Number] {
      override def changed(observable: ObservableValue[_ <: Number], oldValue: Number, newValue: Number): Unit = {
        countries.foreach { case (name, country) =>
          AnchorPane.setTopAnchor(country, country.origPoints.minBy(_._2)._2 * bgYScale.get)
            country.resizePoly()
        }
      }
    })
  }




  def init(): Unit = {
    styleMap()
    scaleMap()
    initCountries()
    players.zipWithIndex.foreach{e => e._1.init(e._2 + 1)}

    //DEBUG
    enableDebug()
  }
}