package GUI

import java.lang
import javafx.beans.value.{ObservableValue, ChangeListener}
import javafx.event.{ActionEvent, EventHandler}
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Paint

import Service._

import scala.collection.mutable
import scala.util.Random
import scalafx.animation.{PauseTransition, TranslateTransition}
import scalafx.geometry.Pos
import scalafx.scene.Group
import scalafx.scene.layout._
import scalafx.scene.paint.Color
import scalafx.scene.shape.SVGPath
import scalafx.stage.Screen
import scalafx.util.Duration

/**
  * Created by Harambe on 7/31/2017.
  */

class Dice(val index: Int) {

  val diceShapes = List(
    "M50.498,38.498c0,6.627-5.373,12-12,12H12.5c-6.629,0-12.001-5.373-12.001-12V12.499c0-6.627,5.372-12,12.001-12h25.998c6.627,0,12,5.373,12,12V38.498 M25.446,30.499c2.488,0,5-2.512,5-5c0-2.487-2.512-5-5-5h0.105c-2.486,0-5,2.512-5,5c0,2.487,2.512,5,5,5H25.446z",
    "M50.498,38.498c0,6.627-5.373,12-12,12H12.5c-6.629,0-12.001-5.373-12.001-12V12.499c0-6.627,5.372-12,12.001-12h25.998c6.627,0,12,5.373,12,12V38.498 M15.771,20.823c2.488,0,5-2.512,5-5c0-2.487-2.512-5-5-5h0.105c-2.486,0-5,2.512-5,5c0,2.487,2.512,5,5,5H15.771 M34.967,40.019c2.488,0,5-2.512,5-5c0-2.487-2.512-5-5-5h0.105c-2.486,0-5,2.512-5,5c0,2.487,2.511,5,5,5H34.967z",
    "M50.498,38.498c0,6.627-5.373,12-12,12H12.5c-6.629,0-12.001-5.373-12.001-12V12.499c0-6.627,5.372-12,12.001-12h25.998c6.627,0,12,5.373,12,12V38.498M12.947,18c2.488,0,5-2.512,5-5c0-2.487-2.512-5-5-5h0.105c-2.486,0-5,2.512-5,5c0,2.487,2.512,5,5,5H12.947M25.446,30.5c2.488,0,5-2.512,5-5c0-2.487-2.512-5-5-5h0.105c-2.486,0-5,2.512-5,5c0,2.487,2.511,5,5,5H25.446M37.947,43c2.488,0,5-2.512,5-5c0-2.487-2.512-5-5-5h0.105c-2.486,0-5,2.512-5,5c0,2.487,2.512,5,5,5H37.947z",
    "M50.498,38.498c0,6.627-5.373,12-12,12H12.5c-6.629,0-12.001-5.373-12.001-12V12.499c0-6.627,5.372-12,12.001-12h25.998c6.627,0,12,5.373,12,12V38.498M12.947,18c2.488,0,5-2.512,5-5c0-2.487-2.512-5-5-5h0.105c-2.486,0-5,2.512-5,5c0,2.487,2.512,5,5,5H12.947M12.946,42.999c2.488,0,5-2.512,5-5c0-2.487-2.512-5-5-5h0.105c-2.486,0-5,2.512-5,5c0,2.487,2.511,5,5,5H12.946M37.946,17.999c2.488,0,5-2.512,5-5c0-2.487-2.512-5-5-5h0.105c-2.486,0-4.999,2.512-4.999,5c0,2.487,2.511,5,4.999,5H37.946M37.947,43c2.488,0,5-2.512,5-5c0-2.487-2.512-5-5-5h0.105c-2.486,0-5,2.512-5,5c0,2.487,2.512,5,5,5H37.947z",
    "M50.498,38.498c0,6.627-5.373,12-12,12H12.5c-6.629,0-12.001-5.373-12.001-12V12.499c0-6.627,5.372-12,12.001-12h25.998c6.627,0,12,5.373,12,12V38.498M12.947,18c2.488,0,5-2.512,5-5c0-2.487-2.512-5-5-5h0.105c-2.486,0-5,2.512-5,5c0,2.487,2.512,5,5,5H12.947M25.446,30.499c2.488,0,5-2.512,5-5c0-2.487-2.512-5-5-5h0.105c-2.486,0-5,2.512-5,5c0,2.487,2.512,5,5,5H25.446M12.946,42.999c2.488,0,5-2.512,5-5c0-2.487-2.512-5-5-5h0.105c-2.486,0-5,2.512-5,5c0,2.487,2.511,5,5,5H12.946M37.946,17.999c2.488,0,5-2.512,5-5c0-2.487-2.512-5-5-5h0.105c-2.486,0-4.999,2.512-4.999,5c0,2.487,2.511,5,4.999,5H37.946M37.947,43c2.488,0,5-2.512,5-5c0-2.487-2.512-5-5-5h0.105c-2.486,0-5,2.512-5,5c0,2.487,2.512,5,5,5H37.947z",
    "M50.498,38.498c0,6.627-5.373,12-12,12H12.5c-6.629,0-12.001-5.373-12.001-12V12.499c0-6.627,5.372-12,12.001-12h25.998c6.627,0,12,5.373,12,12V38.498M12.947,16c2.488,0,5-2.512,5-5c0-2.487-2.512-5-5-5h0.105c-2.486,0-5,2.512-5,5c0,2.487,2.512,5,5,5H12.947M12.947,30.499c2.488,0,5-2.512,5-5c0-2.487-2.512-5-5-5h0.105c-2.486,0-5,2.512-5,5c0,2.487,2.512,5,5,5H12.947M35.947,30.499c2.488,0,5-2.512,5-5c0-2.487-2.512-5-5-5h0.105c-2.486,0-5,2.512-5,5c0,2.487,2.512,5,5,5H35.947M12.947,45c2.488,0,5-2.512,5-5c0-2.487-2.512-5-5-5h0.105c-2.486,0-5,2.512-5,5c0,2.487,2.512,5,5,5H12.947M35.947,16c2.488,0,5-2.512,5-5c0-2.487-2.512-5-5-5h0.105c-2.486,0-5,2.512-5,5c0,2.487,2.512,5,5,5H35.947M35.947,45c2.488,0,5-2.512,5-5c0-2.487-2.512-5-5-5h0.105c-2.486,0-5,2.512-5,5c0,2.487,2.512,5,5,5H35.947z"
  )

  var shape: SVGPath = new SVGPath()
  var isToggledOn: Boolean = true

  shape.setContent(diceShapes.head)
  shape.setPickOnBounds(true)

  def toggleOn() = {
    shape.setOpacity(1)
    isToggledOn = true
  }

  def toggleOff() = {
    shape.setOpacity(.3)
    isToggledOn = false
  }

  def toggle() = isToggledOn = !isToggledOn
}



class BattleDisplayConsole(wmUICont: WorldMapUIController) extends DisplayConsole {

  private val self = this
  private var _attackingCountry: Option[Country] = None
  private var _defendingCountry: Option[Country] = None

  //Dice 1-6 SvgPath commands


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
      rollAnim()
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
  }


  def setDiceColor(attColor: Paint, defColor: Paint): Unit = {
    diceArray(0).shape.setFill(attColor)
    diceArray(1).shape.setFill(attColor)
    diceArray(2).shape.setFill(attColor)

    diceArray(3).shape.setFill(defColor)
    diceArray(4).shape.setFill(defColor)
  }

  def startBattle(attackingCountry: Country, defendingCountry: Country): Unit = {
    setDiceColor(attackingCountry.owner.get.color, defendingCountry.owner.get.color)
    setDiceActions(attackingCountry.armies, defendingCountry.armies)
    _attackingCountry = Some(attackingCountry)
    _defendingCountry = Some(defendingCountry)
    openAnim()
  }

  def endBattle(): Unit = {
    println("Battle Ending")
    diceArray.foreach(deactivateDice)
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
      diceArray(3).toggleOn
      diceArray(4).toggleOff
    }
    if (defArmies > 1) {
      diceArray(3).toggleOn
      diceArray(3).toggleOn
    }
  }


  def activateDice(dice: Dice): Unit = {
    dice.shape.setOnMouseClicked(new EventHandler[MouseEvent] {
      override def handle(event: MouseEvent): Unit = {
        diceArray(dice.index) = dice.toggle
      }
    })
  }

  def deactivateDice(dice: Dice): Unit = {
    dice.shape.setOnMouseClicked(new EventHandler[MouseEvent] {
      override def handle(event: MouseEvent): Unit = {}
    })
    if (dice.isToggledOn)
      diceArray(dice.index) = dice.toggle
  }

  def rollAnim(): Unit = {
    println("Roll animation starting")
    val dice = activeDice.toList.filter(_._2)
    var currentRoll = 0

    val roll = new PauseTransition()
    roll.duration = new Duration(100)
    roll.onFinished = new EventHandler[ActionEvent] {
      override def handle(event: ActionEvent): Unit = {
        if (currentRoll < 20) {
          println("Rolling within 20")
          currentRoll += 1
          dice.foreach(_._1.setContent(diceShapes(Random.nextInt(6))))
          roll.playFromStart()
        }
        else {
          println("Updating die correctly")
          println(" Offensive Rolls: ")
          wmUICont.receiveInput(ConfirmBattle(_attackingCountry.get, _defendingCountry.get, activeDice.take(3).count(_._2)))
          wmUICont.getPhase match {
            case Battle(_, _, _, trans) if trans =>


            case Battle(_, _, pB, _) =>
              pB.get.offRolls.foldLeft(0) { case (i, rollNum) =>
                activeDice(i)._1.setContent(diceShapes(rollNum - 1))
                println(s"Offensive dice $i: $rollNum")
                i + 1
              }

              pB.get.defRolls.foldLeft(3) { case (i, rollNum) =>
                activeDice(i)._1.setContent(diceShapes(rollNum - 1))
                println(s"Defensive dice $i: $rollNum")
                i + 1
              }

          }
        }
      }
    }
    roll.playFromStart()
    roll.
  }

  def postInit(): Unit = {
    translateY.set(height.value * -1)
  }


  val attackingDieBox = new HBox()
  attackingDieBox.spacing = 10
  attackingDieBox.alignment = Pos.Center
  attackingDieBox.children.addAll(new Group(diceArray(0).shape _), new Group(diceArray(1)), new Group(diceArray(2)))

  val defendingDieBox = new HBox()
  defendingDieBox.spacing = 10
  defendingDieBox.alignment = Pos.Center
  defendingDieBox.children.addAll(new Group(defDice1), new Group(defDice2))

  val navigationBox = new HBox()
  navigationBox.spacing = 10
  navigationBox.alignment = Pos.Center
  navigationBox.children.addAll(confirmButton, cancelButton)


  val layoutBox = new VBox()

  layoutBox.spacing = 20
  layoutBox.alignment = Pos.Center
  layoutBox.children.addAll(attackingDieBox, defendingDieBox, navigationBox)
  val contentGroup = new Group(layoutBox)


  this.alignment = Pos.Center
  this.styleClass.add("displayConsole")
  this.children.add(contentGroup)
}
