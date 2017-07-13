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

  val countriesUI = initCountries(wm.countries)


  this.widthProperty().addListener(new ChangeListener[Number] {
    override def changed(observable: ObservableValue[_ <: Number], oldValue: Number, newValue: Number): Unit = {
      countriesUI.foreach { c =>
        AnchorPane.setLeftAnchor(c, c.origPoints.minBy(_._1)._1 * bgXScale.get())
        c.resizePoly()
      }
    }
  })

  this.heightProperty().addListener(new ChangeListener[Number] {
    override def changed(observable: ObservableValue[_ <: Number], oldValue: Number, newValue: Number): Unit = {
      countriesUI.foreach { c =>
        AnchorPane.setTopAnchor(c, c.origPoints.minBy(_._2)._2 * bgYScale.get)
        c.resizePoly()
      }
    }
  })

  def initCountries(countries: List[Country]): List[CountryUI] = {
    countries.map { c =>
      val ui = new CountryUI(c, PixelDatabase.lookup(c.name))
      this.children.add(ui)
      ui.initShape(bgXScale, bgYScale)
      AnchorPane.setTopAnchor(ui, ui.origPoints.minBy(_._2)._2 * bgYScale.get())
      AnchorPane.setLeftAnchor(ui, ui.origPoints.minBy(_._1)._1 * bgXScale.get())
      ui.resizePoly()
      ui
    }
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

  def update(): Unit = {
    initCountries(wm.countries)
  }

  styleMap()
  scaleMap()
}
