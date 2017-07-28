package GUI

import javafx.beans.property.{DoubleProperty, SimpleDoubleProperty}
import javafx.beans.value.{ObservableValue, ChangeListener}
import javafx.geometry.Bounds
import javafx.scene.paint.Paint

import scalafx.animation.{KeyValue, Timeline, KeyFrame}
import scalafx.scene.shape.{Line, Shape, SVGPath}
import scalafx.util.Duration
import scalaz._
import Scalaz._


import Service.{Battle, Attacking, Phase}

import scalafx.geometry.{ Pos}
import scalafx.scene.Group
import scalafx.scene.layout.{Priority, VBox, StackPane, HBox}
import scalafx.scene.text.Text

/**
  * Created by Harambe on 7/27/2017.
  */

class EnhancedArmyDisplay(xScale: SimpleDoubleProperty, yScale: SimpleDoubleProperty) extends HBox {


//  val minusButton = new SVGPath()
//  minusButton.setContent("M18.967,44.382c-9.706-9.704-9.706-29.308,0-39.013H7.779 c-9.705,9.705-9.705,29.309,0,39.013H18.967z")
//  minusButton.scaleX.bind(xScale)
//  minusButton.scaleY.bind(yScale)
//  val minusButtonGroup = new Group(minusButton)

  val displayCircle = new SVGPath()
  displayCircle.setContent("M40.251,0.5c-15.768,0-24.957,12.625-24.957,24.749 c0,21.001,17.332,25.251,24.957,25.251c12,0,25.043-8.27,25.043-25.251C65.294,11.493,54.188,0.5,40.251,0.5z")
  displayCircle.setScaleX(.8)
  displayCircle.setScaleY(.8)
  displayCircle.setFill(CustomColors.gray)
  val circleCenterX = new SimpleDoubleProperty()
  val circleCenterY = new SimpleDoubleProperty()
  displayCircle.boundsInParent.addListener(new ChangeListener[Bounds] {
    override def changed(observable: ObservableValue[_ <: Bounds], oldValue: Bounds, newValue: Bounds): Unit = {
      circleCenterX.setValue(newValue.getMaxX - (newValue.getWidth /2))
      circleCenterY.setValue(newValue.getMaxY - (newValue.getHeight /2))
    }
  })


  val armyText = new Text("0")
  armyText.setScaleX(2)
  armyText.setScaleY(2)

  val displayContainer = new StackPane()
  displayContainer.children.addAll(displayCircle, armyText)
  displayContainer.alignment = Pos.Center
  displayContainer.scaleX.bind(xScale)
  displayContainer.scaleY.bind(yScale)
  val displayContainerGroup = new Group(displayContainer)


  val plusButton = new SVGPath()
  plusButton.setContent("M60.61,5.368c9.706,9.704,9.706,29.308,0,39.013h11.188 c9.705-9.705,9.705-29.309,0-39.013H60.61z")
  plusButton.scaleX.bind(xScale)
  plusButton.scaleY.bind(yScale)
  val plusButtonGroup = new Group(plusButton)



  alignment = Pos.Center
  this.children.addAll(minusButton, displayContainerGroup, plusButton)
}

class SVGArmyDisplay(xScale: SimpleDoubleProperty, yScale: SimpleDoubleProperty) extends VBox {

  def isEnhanced: Boolean = isAttackEnhanced || isTransferEnhanced
  private var isAttackEnhanced = false
  private var isTransferEnhanced = false

  val enhancedShape = new EnhancedArmyDisplay(xScale, yScale)
  enhancedShape.setScaleY(.1)
  enhancedShape.setScaleY(.1)


  val displayCircle = new SVGPath()
  displayCircle.setContent("M40.251,0.5c-15.768,0-24.957,12.625-24.957,24.749 c0,21.001,17.332,25.251,24.957,25.251c12,0,25.043-8.27,25.043-25.251C65.294,11.493,54.188,0.5,40.251,0.5z")
  displayCircle.setScaleX(.8)
  displayCircle.setScaleY(.8)
  displayCircle.setFill(CustomColors.gray)

  val armyText = new Text("0")
  armyText.setScaleX(2)
  armyText.setScaleY(2)

  val displayContainer = new StackPane()
  displayContainer.children.addAll(displayCircle, armyText)
  displayContainer.alignment = Pos.Center
  displayContainer.scaleXProperty().bind(xScale)
  displayContainer.scaleYProperty().bind(yScale)

  val displayGroup = new Group(displayContainer)



  def update(color: Paint, armies: Int, isSourceCountry: Boolean, isAttacking: Boolean, isTransferring: Boolean): Unit = {
    if(isSourceCountry)
      println("Starting source country updated")

    displayCircle.setFill(color)
    armyText.setText(armies.toString)
    checkForEnhanceTransformations(isSourceCountry, isAttacking, isTransferring)
  }

  def checkForEnhanceTransformations(isSourceCountry: Boolean, isAttacking: Boolean, isTranferring: Boolean): Unit = isSourceCountry ? checkForEnhancements(isAttacking, isTranferring) | checkForNoEnhancements()

  def checkForEnhancements(isAttacking: Boolean, isTransferring: Boolean): Unit = {
    println("Source country enhancement")
    if (isTransferring) {
      checkForTransferEnhancements()
    }
    else if(isAttacking)
      checkForAttackEnhancements()
  }

  def checkForAttackEnhancements(): Unit = {
    println("Checking for attack enhancements")
    if (!isAttackEnhanced)
      noEnhanceToAttackEnhance()

  }

  def checkForTransferEnhancements(): Unit = {
    println("Checking for transfer enhancements")
    if (!isTransferEnhanced) {
      if (isAttackEnhanced) attackEnhanceToTranferEnhance()
      else if (!isEnhanced) noEnhanceToTransferEnhance()
    }
  }

  def checkForNoEnhancements(): Unit = {
    if (isEnhanced) {
      checkForNoTransferEnhance()
      checkForNoAttackEnhance()
    }
  }

  def checkForNoTransferEnhance(): Unit = if (isTransferEnhanced) transferEnhanceToNoEnhance()

  def checkForNoAttackEnhance(): Unit = if (isAttackEnhanced) attackEnhanceToNoEnhance()


  def noEnhanceToAttackEnhance(): Unit = {
    //transform the current circleDisplay into a square and translate it down

    isAttackEnhanced = true
    println("Starting animation")

    children.add(0, enhancedShape)

    val shrinkKF = KeyFrame(new Duration(500), values =  Set(KeyValue(displayGroup.scaleX,  displayContainer.scaleX.value / 2), KeyValue(displayGroup.scaleY,  displayContainer.scaleY.value / 2)))
    val expandEnhancedKF = KeyFrame(new Duration(500), values = Set(KeyValue(enhancedShape.scaleX, 1.2), KeyValue(enhancedShape.scaleY, 1.2)))
    val slamEnhancedKF = KeyFrame(new Duration(700), values =  Set(KeyValue(enhancedShape.scaleX, 1), KeyValue(enhancedShape.scaleX, 1)))
    val timeline = Timeline(Seq(shrinkKF, expandEnhancedKF, slamEnhancedKF))


    timeline.play()
  }

  def noEnhanceToTransferEnhance(): Unit = {

  }

  def attackEnhanceToTranferEnhance(): Unit = {

  }

  def attackEnhanceToNoEnhance(): Unit = {
    val shrinkKF = KeyFrame(new Duration(500), values =  Set(KeyValue(displayGroup.scaleX,  displayContainer.scaleX.value / 2), KeyValue(displayGroup.scaleY,  displayContainer.scaleY.value / 2)))
    val expandEnhancedKF = KeyFrame(new Duration(500), values = Set(KeyValue(enhancedShape.scaleX, 1.2), KeyValue(enhancedShape.scaleY, 1.2)))
    val slamEnhancedKF = KeyFrame(new Duration(700), values =  Set(KeyValue(enhancedShape.scaleX, 1), KeyValue(enhancedShape.scaleX, 1)))
    val timeline = Timeline(Seq(shrinkKF, expandEnhancedKF, slamEnhancedKF))
    timeline.setAutoReverse(true)

    timeline.playFrom(new Duration(700))
  }

  def transferEnhanceToNoEnhance(): Unit = {

  }


  this.setMouseTransparent(true)
  alignment = Pos.Center
  children.addAll(displayGroup)
}