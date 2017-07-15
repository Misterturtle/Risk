import scalafx.scene.layout.AnchorPane
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.event.EventHandler
import javafx.scene.input.MouseEvent

import scala.util.Random
import scalafx.scene.image.Image
import scalafx.scene.layout.AnchorPane
import scalafx.scene.paint.Color


class WorldMapUI(wm: WorldMap) extends AnchorPane {

  val bgXScale = new SimpleDoubleProperty()
  val bgYScale = new SimpleDoubleProperty()

  var countriesUI = initCountries(wm.countries)

  val


  this.widthProperty().addListener(new ChangeListener[Number] {
    override def changed(observable: ObservableValue[_ <: Number], oldValue: Number, newValue: Number): Unit = {
      countriesUI.foreach {case (name, c) =>
        AnchorPane.setLeftAnchor(c, c.origPoints.minBy(_._1)._1 * bgXScale.get())
        c.resizePoly()
      }
    }
  })

  this.heightProperty().addListener(new ChangeListener[Number] {
    override def changed(observable: ObservableValue[_ <: Number], oldValue: Number, newValue: Number): Unit = {
      countriesUI.foreach {case  (name,c) =>
        AnchorPane.setTopAnchor(c, c.origPoints.minBy(_._2)._2 * bgYScale.get)
        c.resizePoly()
      }
    }
  })

  def initCountries(countries: List[Country]): Map[String,CountryUI] = {
    countries.map { c =>
      val ui = new CountryUI(c, PixelDatabase.lookup(c.name))
      this.children.add(ui)
      ui.initShape(bgXScale, bgYScale)
      AnchorPane.setTopAnchor(ui, ui.origPoints.minBy(_._2)._2 * bgYScale.get())
      AnchorPane.setLeftAnchor(ui, ui.origPoints.minBy(_._1)._1 * bgXScale.get())
      ui.resizePoly()
      ui.drawDebug()
      (c.name, ui)
    }.toMap
  }

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


  }

  def updateWorldMap(worldMap:WorldMap): Unit = {
    worldMap.countries.foreach{c => countriesUI(c.name).update(c)}
  }

  styleMap()
  scaleMap()
  enableDebug()
}
