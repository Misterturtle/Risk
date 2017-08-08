package GUI

import java.lang
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.event.{ActionEvent, EventHandler}
import javafx.geometry.Insets
import javafx.scene.input.MouseEvent
import javafx.scene.layout.{CornerRadii, BackgroundFill, Background}
import javafx.scene.paint.Paint

import Service._

import scalafx.beans.property.ReadOnlyDoubleProperty
import scalafx.geometry
import scalaz.Scalaz._
import scala.util.Random
import scalafx.animation.{Animation, PauseTransition, TranslateTransition}
import scalafx.geometry.Pos
import scalafx.scene.Group
import scalafx.scene.layout._
import scalafx.scene.paint.Color
import scalafx.scene.shape.SVGPath
import scalafx.util.Duration

/**
  * Created by Harambe on 7/31/2017.
  */

object Dice{

  def setDiceDisplay(dice:Dice, numberRolled: Int): Unit ={
    dice.svgShape.setContent(diceShapes(numberRolled - 1))
  }

  val diceShapes = List(
    "M50.498,38.498c0,6.627-5.373,12-12,12H12.5c-6.629,0-12.001-5.373-12.001-12V12.499c0-6.627,5.372-12,12.001-12h25.998c6.627,0,12,5.373,12,12V38.498 M25.446,30.499c2.488,0,5-2.512,5-5c0-2.487-2.512-5-5-5h0.105c-2.486,0-5,2.512-5,5c0,2.487,2.512,5,5,5H25.446z",
    "M50.498,38.498c0,6.627-5.373,12-12,12H12.5c-6.629,0-12.001-5.373-12.001-12V12.499c0-6.627,5.372-12,12.001-12h25.998c6.627,0,12,5.373,12,12V38.498 M15.771,20.823c2.488,0,5-2.512,5-5c0-2.487-2.512-5-5-5h0.105c-2.486,0-5,2.512-5,5c0,2.487,2.512,5,5,5H15.771 M34.967,40.019c2.488,0,5-2.512,5-5c0-2.487-2.512-5-5-5h0.105c-2.486,0-5,2.512-5,5c0,2.487,2.511,5,5,5H34.967z",
    "M50.498,38.498c0,6.627-5.373,12-12,12H12.5c-6.629,0-12.001-5.373-12.001-12V12.499c0-6.627,5.372-12,12.001-12h25.998c6.627,0,12,5.373,12,12V38.498M12.947,18c2.488,0,5-2.512,5-5c0-2.487-2.512-5-5-5h0.105c-2.486,0-5,2.512-5,5c0,2.487,2.512,5,5,5H12.947M25.446,30.5c2.488,0,5-2.512,5-5c0-2.487-2.512-5-5-5h0.105c-2.486,0-5,2.512-5,5c0,2.487,2.511,5,5,5H25.446M37.947,43c2.488,0,5-2.512,5-5c0-2.487-2.512-5-5-5h0.105c-2.486,0-5,2.512-5,5c0,2.487,2.512,5,5,5H37.947z",
    "M50.498,38.498c0,6.627-5.373,12-12,12H12.5c-6.629,0-12.001-5.373-12.001-12V12.499c0-6.627,5.372-12,12.001-12h25.998c6.627,0,12,5.373,12,12V38.498M12.947,18c2.488,0,5-2.512,5-5c0-2.487-2.512-5-5-5h0.105c-2.486,0-5,2.512-5,5c0,2.487,2.512,5,5,5H12.947M12.946,42.999c2.488,0,5-2.512,5-5c0-2.487-2.512-5-5-5h0.105c-2.486,0-5,2.512-5,5c0,2.487,2.511,5,5,5H12.946M37.946,17.999c2.488,0,5-2.512,5-5c0-2.487-2.512-5-5-5h0.105c-2.486,0-4.999,2.512-4.999,5c0,2.487,2.511,5,4.999,5H37.946M37.947,43c2.488,0,5-2.512,5-5c0-2.487-2.512-5-5-5h0.105c-2.486,0-5,2.512-5,5c0,2.487,2.512,5,5,5H37.947z",
    "M50.498,38.498c0,6.627-5.373,12-12,12H12.5c-6.629,0-12.001-5.373-12.001-12V12.499c0-6.627,5.372-12,12.001-12h25.998c6.627,0,12,5.373,12,12V38.498M12.947,18c2.488,0,5-2.512,5-5c0-2.487-2.512-5-5-5h0.105c-2.486,0-5,2.512-5,5c0,2.487,2.512,5,5,5H12.947M25.446,30.499c2.488,0,5-2.512,5-5c0-2.487-2.512-5-5-5h0.105c-2.486,0-5,2.512-5,5c0,2.487,2.512,5,5,5H25.446M12.946,42.999c2.488,0,5-2.512,5-5c0-2.487-2.512-5-5-5h0.105c-2.486,0-5,2.512-5,5c0,2.487,2.511,5,5,5H12.946M37.946,17.999c2.488,0,5-2.512,5-5c0-2.487-2.512-5-5-5h0.105c-2.486,0-4.999,2.512-4.999,5c0,2.487,2.511,5,4.999,5H37.946M37.947,43c2.488,0,5-2.512,5-5c0-2.487-2.512-5-5-5h0.105c-2.486,0-5,2.512-5,5c0,2.487,2.512,5,5,5H37.947z",
    "M50.498,38.498c0,6.627-5.373,12-12,12H12.5c-6.629,0-12.001-5.373-12.001-12V12.499c0-6.627,5.372-12,12.001-12h25.998c6.627,0,12,5.373,12,12V38.498M12.947,16c2.488,0,5-2.512,5-5c0-2.487-2.512-5-5-5h0.105c-2.486,0-5,2.512-5,5c0,2.487,2.512,5,5,5H12.947M12.947,30.499c2.488,0,5-2.512,5-5c0-2.487-2.512-5-5-5h0.105c-2.486,0-5,2.512-5,5c0,2.487,2.512,5,5,5H12.947M35.947,30.499c2.488,0,5-2.512,5-5c0-2.487-2.512-5-5-5h0.105c-2.486,0-5,2.512-5,5c0,2.487,2.512,5,5,5H35.947M12.947,45c2.488,0,5-2.512,5-5c0-2.487-2.512-5-5-5h0.105c-2.486,0-5,2.512-5,5c0,2.487,2.512,5,5,5H12.947M35.947,16c2.488,0,5-2.512,5-5c0-2.487-2.512-5-5-5h0.105c-2.486,0-5,2.512-5,5c0,2.487,2.512,5,5,5H35.947M35.947,45c2.488,0,5-2.512,5-5c0-2.487-2.512-5-5-5h0.105c-2.486,0-5,2.512-5,5c0,2.487,2.512,5,5,5H35.947z"
  )
}

class Dice(val index: Int) extends HBox {

  var svgShape: SVGPath = new SVGPath()
  var isToggledOn: Boolean = true

  svgShape.setContent(Dice.diceShapes.head)
  svgShape.setPickOnBounds(true)

  def toggleOn() = {
    this.setOpacity(1)
    isToggledOn = true
  }

  def toggleOff() = {
    this.setOpacity(.3)
    isToggledOn = false
  }

  def toggle() = isToggledOn ? toggleOff() | toggleOn()

  children.add(svgShape)
  this.setBackground(new Background(new BackgroundFill(javafx.scene.paint.Color.WHITE, CornerRadii.EMPTY, new Insets(5))))
}


class BattleDisplayConsole(wmUICont: WorldMapUIController, val sceneWidth: ReadOnlyDoubleProperty, val sceneHeight: ReadOnlyDoubleProperty, val windowXScale:SimpleDoubleProperty, val windowYScale: SimpleDoubleProperty) extends DisplayConsole {

  private val self = this
  private var _attackingCountry: Option[Country] = None
  private var _defendingCountry: Option[Country] = None

  val diceArray = new Array[Dice](5)
  diceArray(0) = new Dice(0)
  diceArray(1) = new Dice(1)
  diceArray(2) = new Dice(2)
  diceArray(3) = new Dice(3)
  diceArray(4) = new Dice(4)


  val confirmButton = new SVGPath()
  confirmButton.setContent("M46.652,17.374c-13.817,0-24.999,11.182-25,25\n\tc0,13.818,11.182,25,25,25c13.819,0,25.001-11.182,25.001-25S60.472,17.374,46.652,17.374z M44.653,55.373c-6-3-11-6.999-16-9.999\n\tc1.347-3,2.722-4.499,5.722-5.499c2,4,6.278,3.499,8.278,6.499c5-6,10-12,16-17c2,0,3,3,6,3C63,36.625,48.653,45.374,44.653,55.373z")
  confirmButton.setFill(Color.valueOf("#91DC5A"))
  confirmButton.setPickOnBounds(true)
  confirmButton.setOnMouseClicked(new EventHandler[MouseEvent] {
    override def handle(event: MouseEvent): Unit = {
      roll()
    }
  })

  val cancelButton = new SVGPath()
  cancelButton.setContent("M25.5,0.5c-13.816,0-24.999,11.182-25,25c0,13.818,11.181,25,25,25\n\tc13.82,0,25.002-11.182,25.002-25C50.501,11.682,39.32,0.5,25.5,0.5z M36.105,43.179l-10.609-10.61l-9.9,9.899l-7.07-7.069l9.9-9.9\n\tl-10.6-10.61l7.071-7.07l10.609,10.61l10.32-10.32l7.07,7.07l-10.33,10.32l10.609,10.61L36.105,43.179z")
  cancelButton.setFill(Color.valueOf("#FF0000"))
  cancelButton.setPickOnBounds(true)
  cancelButton.setOnMouseClicked(new EventHandler[MouseEvent] {
    override def handle(event: MouseEvent): Unit = {
      wmUICont.receiveInput(Retreat)
      closeAnim()
    }
  })


  def update(attackingCountry: Country, defendingCountry: Country, previousBattle: Option[BattleResult]): Unit = {
    setDiceActions(attackingCountry.armies, defendingCountry.armies)
    _attackingCountry = Some(attackingCountry)
    _defendingCountry = Some(defendingCountry)
    displayBattleResults(previousBattle)
  }

  def displayBattleResults(previousBattle: Option[BattleResult]): Unit ={
    if(previousBattle.nonEmpty){
      var offDiceCounter = 0
      var defDiceCounter = 0
      previousBattle.get.offRolls.foreach {d =>
        Dice.setDiceDisplay(diceArray(offDiceCounter), d)
        offDiceCounter += 1
      }
      previousBattle.get.defRolls.foreach {d =>
        Dice.setDiceDisplay(diceArray(defDiceCounter+3), d)
        defDiceCounter += 1
      }
    }
  }


  def setDiceColor(attColor: Paint, defColor: Paint): Unit = {
    diceArray(0).svgShape.setFill(attColor)
    diceArray(1).svgShape.setFill(attColor)
    diceArray(2).svgShape.setFill(attColor)

    diceArray(3).svgShape.setFill(defColor)
    diceArray(4).svgShape.setFill(defColor)
  }

  def startBattle(attackingCountry: Country, defendingCountry: Country): Unit = {
    setDiceColor(attackingCountry.owner.get.color, defendingCountry.owner.get.color)
    setDiceActions(attackingCountry.armies, defendingCountry.armies)
    _attackingCountry = Some(attackingCountry)
    _defendingCountry = Some(defendingCountry)
    val bg = new Background(new BackgroundFill(attackingCountry.owner.get.color, new CornerRadii(0,0,100,100,false), Insets.EMPTY))
    this.setBackground(bg)
    openAnim()
    ascendAnim.setOnFinished(new EventHandler[ActionEvent] {
      override def handle(event: ActionEvent): Unit = {}
    })
  }

  def endBattle(): Unit = {
    diceArray.foreach(_.svgShape.setOnMouseClicked(new EventHandler[MouseEvent] {
      override def handle(event: MouseEvent) = {}
    }))
    closeAnim()
  }

  def setDiceActions(attArmies: Int, defArmies: Int): Unit = {
    if (attArmies == 2) {
      deactivateDice(diceArray(1))
      deactivateDice(diceArray(2))
    }
    if (attArmies == 3) {
      activateDice(diceArray(1))
      deactivateDice(diceArray(2))
    }
    if (attArmies > 3) {
      activateDice(diceArray(1))
      activateDice(diceArray(2))
    }

    if (defArmies == 1) {
      diceArray(3).toggleOn()
      diceArray(4).toggleOff()
    }
    if (defArmies > 1) {
      diceArray(3).toggleOn()
      diceArray(3).toggleOn()
    }
  }


  def activateDice(dice: Dice): Unit = {
    dice.svgShape.setOnMouseClicked(new EventHandler[MouseEvent] {
      override def handle(event: MouseEvent): Unit = {
        dice.toggle
      }
    })
    dice.toggleOn()
  }

  def deactivateDice(dice: Dice): Unit = {
    dice.svgShape.setOnMouseClicked(new EventHandler[MouseEvent] {
      override def handle(event: MouseEvent): Unit = {}
    })
    dice.toggleOff()
  }

  def roll(): Unit = {
    cancelButton.setOnMouseClicked(new EventHandler[MouseEvent] {
      override def handle(event: MouseEvent): Unit = {}
    })

    val dice = diceArray.toList.filter(_.isToggledOn)
    var currentRoll = 0

    val rollCycle = new PauseTransition()
    rollCycle.duration = new Duration(100)
    rollCycle.onFinished = new EventHandler[ActionEvent] {
      override def handle(event: ActionEvent): Unit = {
        dice.foreach(d => d.svgShape.setContent(Dice.diceShapes(Random.nextInt(6))))
        if(currentRoll == 20){
          cancelButton.setOnMouseClicked(new EventHandler[MouseEvent] {
            override def handle(event: MouseEvent): Unit = {
              wmUICont.receiveInput(Retreat)
              closeAnim()
            }
          })
          wmUICont.receiveInput(ConfirmBattle(_attackingCountry.get, _defendingCountry.get, diceArray.take(3).count(_.isToggledOn)))
        }
        else{
          currentRoll += 1
          rollCycle.playFromStart()
        }
      }
    }
    rollCycle.playFromStart()
  }






  val attackingDieBox = new HBox(diceArray(0), diceArray(1), diceArray(2))
  attackingDieBox.spacing = 10
  attackingDieBox.alignment = Pos.Center

  val defendingDieBox = new HBox(diceArray(3), diceArray(4))
  defendingDieBox.spacing = 10
  defendingDieBox.alignment = Pos.Center

  val diceBox = new VBox(attackingDieBox, defendingDieBox)
  diceBox.spacing = 20
  diceBox.alignment = Pos.Center

  val navigationBox = new VBox()
  navigationBox.spacing = 20
  navigationBox.alignment = Pos.Center
  navigationBox.children.addAll(confirmButton, cancelButton)


  val layoutBox = new HBox()

  layoutBox.spacing = 20
  layoutBox.alignment = Pos.Center
  layoutBox.children.addAll(diceBox, navigationBox)
  layoutBox.styleClass.add("displayConsoleContent")

  val contentGroup = new Group(layoutBox)

  scaleContent(windowXScale, windowYScale)
  this.alignment = Pos.Center
  this.styleClass.add("displayConsole")
  this.children.add(contentGroup)
}
