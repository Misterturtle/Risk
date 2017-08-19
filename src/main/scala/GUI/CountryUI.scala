package GUI

import javafx.animation.Interpolator
import javafx.beans.binding.DoubleBinding
import javafx.beans.property.{ReadOnlyDoubleProperty, SimpleDoubleProperty}
import javafx.beans.value.{ObservableValue, ChangeListener}
import javafx.event.{EventHandler, EventType}
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Paint

import Service._

import scalafx.animation.{RotateTransition, ScaleTransition}
import scalafx.geometry.Pos
import scalafx.scene.Group
import scalafx.scene.layout._
import scalafx.scene.shape.{SVGPath, Circle, Polygon}

import Sugar.CustomSugar._

import scalafx.util.Duration


class CountryUI(countryName:String, val origPoints: List[(Double, Double)], countryClickedInput: () => Unit, mapXScale:SimpleDoubleProperty, mapYScale:SimpleDoubleProperty) extends Group{

  private val self = this
  val origWidth = if (origPoints.nonEmpty) origPoints.maxBy(_._1)._1 - origPoints.minBy(_._1)._1 else 0
  val origHeight = if (origPoints.nonEmpty) origPoints.maxBy(_._2)._2 - origPoints.minBy(_._2)._2 else 0

  val armyDisplay = new SVGArmyDisplay()
  val polygon = new Polygon(new javafx.scene.shape.Polygon())

  val sourceCircleShape = new SVGPath()
  sourceCircleShape.setContent("M0.505,0.005c-0.276,0-0.5,0.224-0.5,0.5c0,0.276,0.224,0.5,0.5,0.5s0.5-0.224,0.5-0.5C1.005,0.229,0.781,0.005,0.505,0.005z M0.505,0.905c-0.221,0-0.4-0.179-0.4-0.4c0-0.221,0.179-0.4,0.4-0.4s0.4,0.179,0.4,0.4C0.905,0.726,0.726,0.905,0.505,0.905z")
  sourceCircleShape.setScaleX(70)
  sourceCircleShape.setScaleY(70)
  sourceCircleShape.mouseTransparent = true
  val sourceCircle = new Group(sourceCircleShape)
  val sourceCircleContainer = new VBox(sourceCircle)

  val targetCircleShape = new SVGPath()
  targetCircleShape.setContent("M1.079,0.555c-0.005-0.112-0.048-0.222-0.128-0.31l0.053-0.054L0.969,0.156L0.915,0.209c-0.088-0.08-0.198-0.123-0.31-0.128V0.005h-0.05v0.076C0.451,0.086,0.348,0.124,0.264,0.193L0.209,0.138L0.173,0.173l0.053,0.053C0.135,0.318,0.087,0.435,0.081,0.555H0.005v0.05h0.076c0.005,0.112,0.048,0.222,0.128,0.31L0.156,0.969l0.035,0.035l0.054-0.053c0.088,0.08,0.198,0.123,0.31,0.128v0.076h0.05V1.079c0.112-0.005,0.222-0.048,0.31-0.128l0.054,0.053l0.035-0.035L0.951,0.915c0.08-0.088,0.123-0.198,0.128-0.31h0.076v-0.05H1.079z M0.863,0.863c-0.156,0.156-0.41,0.156-0.566,0c-0.156-0.156-0.156-0.41,0-0.566c0.156-0.156,0.41-0.156,0.566,0C1.019,0.453,1.019,0.707,0.863,0.863z")
  targetCircleShape.setScaleX(70)
  targetCircleShape.setScaleY(70)
  targetCircleShape.mouseTransparent = true
  val targetCircle = new Group(targetCircleShape)
  val targetCircleContainer = new VBox(targetCircle)

  private var sourceCountryAnimActive = false
  private var targetCountryAnimActive = false
  private val pulseCircleAnim = new ScaleTransition(new Duration(1000))

  private val contentStackPane = new StackPane()
  contentStackPane.children.addAll(polygon, armyDisplay)
  contentStackPane.alignment = Pos.Center
  contentStackPane.scaleX.bind(mapXScale)
  contentStackPane.scaleY.bind(mapYScale)
  //contentStackPane.shape = polygon
  contentStackPane.style = "-fx-border-color: red"


  AnchorPane.setTopAnchor(this, origPoints.minBy(_._2)._2 * mapYScale.get)
  AnchorPane.setLeftAnchor(this, origPoints.minBy(_._1)._1 * mapXScale.get)


  this.children.add(contentStackPane)
  this.mouseTransparent = true





  def update(newCountry:Country): Unit = {
    armyDisplay.update(newCountry.armies, newCountry.owner.map(_.color).getOrElse(CustomColors.gray))
  }

  def initPoly(): Unit = {
    origPoints.foreach { case (x, y) => polygon.getPoints.addAll(x, y) }
    polygon.styleClass.add("polygon")
    polygon.setOnMouseClicked(() => countryClickedInput())
  }

  def resizeXListener(): ChangeListener[Number] ={
    new ChangeListener[Number] {
      override def changed(observable: ObservableValue[_ <: Number], oldValue: Number, newValue: Number): Unit = {
        AnchorPane.setLeftAnchor(self, origPoints.minBy(_._1)._1 * mapXScale.get)
      }
    }
  }

  def resizeYListener(): ChangeListener[Number] = {
    new ChangeListener[Number] {
      override def changed(observable: ObservableValue[_ <: Number], oldValue: Number, newValue: Number): Unit = {
        val minYPoint = origPoints.minBy(_._2)._2
        AnchorPane.setTopAnchor(self,  minYPoint * mapYScale.get)
      }
    }
  }




  def activateSourceCountryAnim(color:Paint): Unit = {
    sourceCircleShape.setFill(color)
    if(!sourceCountryAnimActive){

      sourceCountryAnimActive = true

      contentStackPane.children.add(sourceCircleContainer)

      pulseCircleAnim.setNode(sourceCircle)
      pulseCircleAnim.fromX = .9
      pulseCircleAnim.fromY = .9
      pulseCircleAnim.toX = 1.2
      pulseCircleAnim.toY = 1.2
      pulseCircleAnim.autoReverse = true
      pulseCircleAnim.cycleCount = -1

      pulseCircleAnim.playFromStart()
    }
  }

  def activateTargetCountryAnim(color: Paint): Unit ={
    targetCircleShape.setFill(color)
    if(!targetCountryAnimActive){
      targetCountryAnimActive = true
      contentStackPane.children.add(targetCircleContainer)

      val rotateCircleAnim = new RotateTransition()
      rotateCircleAnim.fromAngle = 0
      rotateCircleAnim.toAngle = 360
      rotateCircleAnim.duration = new Duration(3000)
      rotateCircleAnim.node = targetCircle
      rotateCircleAnim.cycleCount = -1
      rotateCircleAnim.setInterpolator(Interpolator.LINEAR)

      pulseCircleAnim.setNode(targetCircle)
      pulseCircleAnim.fromX = .9
      pulseCircleAnim.fromY = .9
      pulseCircleAnim.toX = 1.2
      pulseCircleAnim.toY = 1.2
      pulseCircleAnim.autoReverse = true
      pulseCircleAnim.cycleCount = -1

      pulseCircleAnim.playFromStart()
      rotateCircleAnim.playFromStart()
    }
  }

  def deactivateAnimations(): Unit ={
    if(sourceCountryAnimActive){
      sourceCountryAnimActive = false
      pulseCircleAnim.stop()
      contentStackPane.children.removeAll(sourceCircleContainer)
    }

    if(targetCountryAnimActive){
      targetCountryAnimActive = false
      pulseCircleAnim.stop()
      contentStackPane.children.removeAll(targetCircleContainer)
    }
  }
}




