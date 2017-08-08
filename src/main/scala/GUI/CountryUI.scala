package GUI

import javafx.beans.property.{ReadOnlyDoubleProperty, SimpleDoubleProperty}
import javafx.beans.value.{ObservableValue, ChangeListener}
import javafx.event.EventHandler
import javafx.scene.Group
import javafx.scene.input.MouseEvent
import javafx.scene.paint.{LinearGradient, Paint}

import Service._

import scalafx.animation.{FillTransition, TranslateTransition}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.layout._
import scalafx.scene.shape.{Circle, Polygon}









class CountryUI(startingCountry: Country, val origPoints: List[(Double, Double)], wmUICont: WorldMapUIController) extends StackPane {

  private val self = this
  val origWidth = if (origPoints.nonEmpty) origPoints.maxBy(_._1)._1 - origPoints.minBy(_._1)._1 else 0
  val origHeight = if (origPoints.nonEmpty) origPoints.maxBy(_._2)._2 - origPoints.minBy(_._2)._2 else 0
  val xScale = new SimpleDoubleProperty()
  val yScale = new SimpleDoubleProperty()

  val armyDisplay = new SVGArmyDisplay(xScale, yScale)
  val polygon = new Polygon(new javafx.scene.shape.Polygon())

  def update(): Unit = {
    armyDisplay.update(startingCountry.name, wmUICont)
    polygon.onMouseClicked = new EventHandler[MouseEvent] {
      override def handle(event: MouseEvent): Unit = {
        wmUICont.receiveInput(CountryClicked(wmUICont.getCountryByName(startingCountry.name)))
      }
    }
  }

  def initPoly(parentPolyXScale: SimpleDoubleProperty, parentPolyYScale: SimpleDoubleProperty): Unit = {
    origPoints.foreach { case (x, y) => polygon.getPoints.addAll(x, y) }
    xScale.bind(parentPolyXScale)
    yScale.bind(parentPolyYScale)
    polygon.scaleX.bind(parentPolyXScale)
    polygon.scaleY.bind(parentPolyYScale)
    polygon.styleClass.add("polygon")
    polygon.setOnMouseClicked(new EventHandler[MouseEvent] {
      override def handle(event: MouseEvent): Unit = {
        wmUICont.receiveInput(CountryClicked(startingCountry))
        println("Clicked "+startingCountry.name)
      }
    })
  }

  def resizeXListener(mapImageXScale: SimpleDoubleProperty): ChangeListener[Number] ={

    new ChangeListener[Number] {
      override def changed(observable: ObservableValue[_ <: Number], oldValue: Number, newValue: Number): Unit = {
        AnchorPane.setLeftAnchor(self, origPoints.minBy(_._1)._1 * mapImageXScale.get)
      }
    }
  }

  def resizeYListener(mapImageYScale: SimpleDoubleProperty): ChangeListener[Number] = {
    new ChangeListener[Number] {
      override def changed(observable: ObservableValue[_ <: Number], oldValue: Number, newValue: Number): Unit = {
        AnchorPane.setTopAnchor(self, origPoints.minBy(_._2)._2 * mapImageYScale.get)
      }
    }
  }

  def isSourceCountry(phase:Phase): Boolean ={
    phase match{
      case Attacking(s, _) =>
        s.map(_.name).getOrElse("None") == startingCountry.name

      case Battle(s,t, pB, trans) =>
        s.name == startingCountry.name

      case _ =>
        false
    }
  }

  styleClass.setAll("country")
  this.setPickOnBounds(false)
  alignment = Pos.Center

  children.add(new Group(polygon))
  children.add(armyDisplay)

}




