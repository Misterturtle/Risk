package GUI

import javafx.beans.binding.DoubleBinding
import javafx.beans.property.ReadOnlyDoubleProperty
import javafx.beans.value.{ObservableValue, ChangeListener}
import javafx.event.{ActionEvent, EventHandler}
import javafx.geometry.Insets
import javafx.scene.input.MouseEvent
import javafx.scene.layout.{CornerRadii, BackgroundFill, Background}
import javafx.scene.paint.Paint

import Service._

import scalaz.Scalaz._
import scala.util.Random
import scalafx.animation.{PauseTransition, TranslateTransition}
import scalafx.geometry.Pos
import scalafx.scene.Group
import scalafx.scene.layout._
import scalafx.scene.paint.Color
import scalafx.scene.shape.SVGPath
import scalafx.util.Duration

import Sugar.CustomSugar._

/**
  * Created by Harambe on 7/31/2017.
  */

object Dice {

  def setDiceDisplay(dice: Dice, numberRolled: Int): Unit = {
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


class BattleConsole(delayForAttCtrl: () => Duration, messageService: (Input) => Unit) extends VBox with PhaseListener with UIController {

  private val self = this
  val diceArray = new Array[Dice](5)
  diceArray(0) = new Dice(0)
  diceArray(1) = new Dice(1)
  diceArray(2) = new Dice(2)
  diceArray(3) = new Dice(3)
  diceArray(4) = new Dice(4)

  private var _isDisplayed = false

  def isDisplayed = _isDisplayed

  private val openAnimation = new TranslateTransition(new Duration(270), this)
  private val closeAnimation = new TranslateTransition(new Duration(270), this)

  def getTimeToFinish: Duration = (closeAnimation.currentTime.value.toMillis == closeAnimation.duration.value.toMillis) ? new Duration(closeAnimation.duration.value) | new Duration(closeAnimation.duration.value.subtract(closeAnimation.currentTime.value))


  val confirmButton = new SVGPath()
  confirmButton.setContent("M46.652,17.374c-13.817,0-24.999,11.182-25,25\n\tc0,13.818,11.182,25,25,25c13.819,0,25.001-11.182,25.001-25S60.472,17.374,46.652,17.374z M44.653,55.373c-6-3-11-6.999-16-9.999\n\tc1.347-3,2.722-4.499,5.722-5.499c2,4,6.278,3.499,8.278,6.499c5-6,10-12,16-17c2,0,3,3,6,3C63,36.625,48.653,45.374,44.653,55.373z")
  confirmButton.setFill(Color.valueOf("#91DC5A"))
  confirmButton.setPickOnBounds(true)
  confirmButton.setOnMouseClicked(() => roll())

  val cancelButton = new SVGPath()
  cancelButton.setContent("M25.5,0.5c-13.816,0-24.999,11.182-25,25c0,13.818,11.181,25,25,25\n\tc13.82,0,25.002-11.182,25.002-25C50.501,11.682,39.32,0.5,25.5,0.5z M36.105,43.179l-10.609-10.61l-9.9,9.899l-7.07-7.069l9.9-9.9\n\tl-10.6-10.61l7.071-7.07l10.609,10.61l10.32-10.32l7.07,7.07l-10.33,10.32l10.609,10.61L36.105,43.179z")
  cancelButton.setFill(Color.valueOf("#FF0000"))
  cancelButton.setPickOnBounds(true)
  cancelButton.setOnMouseClicked(() => {
    messageService(Retreat)
  }
  )

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

  this.alignment = Pos.Center
  this.styleClass.add("displayConsole")
  this.children.add(new Group(contentGroup))

  private def openAnim(onFinish: () => Unit): Unit = {
    openAnimation.setOnFinished(onFinish)
    _isDisplayed = true
    openAnimation.setToY(0)
    this.toFront()
    openAnimation.playFromStart()
  }

  private def closeAnim(onFinish: () => Unit): Unit = {
    closeAnimation.setOnFinished(onFinish)
    _isDisplayed = false
    closeAnimation.setToY(-this.height.get)
    closeAnimation.playFromStart()
  }


  def displayBattleResults(previousBattle: BattleResult): Unit = {
    var offDiceCounter = 0
    var defDiceCounter = 0
    previousBattle.offRolls.foreach { d =>
      Dice.setDiceDisplay(diceArray(offDiceCounter), d)
      offDiceCounter += 1
    }
    previousBattle.defRolls.foreach { d =>
      Dice.setDiceDisplay(diceArray(defDiceCounter + 3), d)
      defDiceCounter += 1
    }
  }


  def activateDice(dice: Dice): Unit = {
    dice.svgShape.setContent(Dice.diceShapes.head)
    dice.svgShape.setOnMouseClicked(() => dice.toggle())
    dice.toggleOn()
  }

  def deactivateDice(dice: Dice): Unit = {
    dice.svgShape.setOnMouseClicked(() => {})
    dice.toggleOff()
  }

  def enableButtons(): Unit = {
    confirmButton.setOnMouseClicked(() => roll())
    cancelButton.setOnMouseClicked(() => {
      messageService(Retreat)
      closeAnim(() => {})
    })
    updateDice()
  }

  def disableButtons(): Unit = {
    cancelButton.setOnMouseClicked(() => {})
    confirmButton.setOnMouseClicked(() => {})
    diceArray.foreach(_.svgShape.setOnMouseClicked(() => {}))
  }

  def updateColor(): Unit = {
    val bg = new Background(new BackgroundFill(_phase.value.asInstanceOf[Battle].source.owner.get.color, new CornerRadii(0, 0, 100, 100, false), Insets.EMPTY))
    this.setBackground(bg)
  }

  def setStartingDice(): Unit = {
    val attColor = _phase.value.asInstanceOf[Battle].source.owner.get.color
    val defColor = _phase.value.asInstanceOf[Battle].target.owner.get.color
    diceArray(0).svgShape.setFill(attColor)
    diceArray(1).svgShape.setFill(attColor)
    diceArray(2).svgShape.setFill(attColor)
    diceArray(3).svgShape.setFill(defColor)
    diceArray(4).svgShape.setFill(defColor)

    val attArmies = _phase.value.asInstanceOf[Battle].source.armies
    val defArmies = _phase.value.asInstanceOf[Battle].target.armies

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
      diceArray(4).toggleOn()
    }
  }

  def updateDice(): Unit = {
    val attArmies = _phase.value.asInstanceOf[Battle].source.armies
    val defArmies = _phase.value.asInstanceOf[Battle].target.armies

    if (attArmies == 2) {
      deactivateDice(diceArray(1))
      deactivateDice(diceArray(2))
    }
    if (attArmies == 3) {
      diceArray(1).svgShape.setOnMouseClicked(() => diceArray(1).toggle())
      deactivateDice(diceArray(2))
    }
    if (attArmies > 3) {
      diceArray(1).svgShape.setOnMouseClicked(() => diceArray(1).toggle())
      diceArray(2).svgShape.setOnMouseClicked(() => diceArray(2).toggle())
    }

    if (defArmies == 1) {
      diceArray(3).toggleOn()
      diceArray(4).toggleOff()
    }
    if (defArmies > 1) {
      diceArray(3).toggleOn()
      diceArray(4).toggleOn()
    }
  }

  def roll(): Unit = {
    disableButtons()
    val dice = diceArray.toList.filter(_.isToggledOn)
    var currentRoll = 0

    val rollCycle = new PauseTransition()
    rollCycle.duration = new Duration(100)
    rollCycle.setOnFinished(() => {
      dice.foreach(d => d.svgShape.setContent(Dice.diceShapes(Random.nextInt(6))))
      if (currentRoll == 20) {
        messageService(ConfirmBattle(_phase.value.asInstanceOf[Battle].source, _phase.value.asInstanceOf[Battle].target,
          diceArray.count(d => d.isToggledOn && d.index <= 2)))
      }
      else {
        currentRoll += 1
        rollCycle.playFromStart()
      }
    })
    rollCycle.playFromStart()
  }

  override def anchorResize(sceneWidth: ReadOnlyDoubleProperty, sceneHeight: ReadOnlyDoubleProperty): Unit = {
    sceneWidth.addListener(new ChangeListener[Number] {
      override def changed(observable: ObservableValue[_ <: Number], oldValue: Number, newValue: Number): Unit = {
        AnchorPane.setLeftAnchor(self, newValue.doubleValue() * .3)
        AnchorPane.setRightAnchor(self, newValue.doubleValue() * .3)
      }
    })

    sceneHeight.addListener(new ChangeListener[Number] {
      override def changed(observable: ObservableValue[_ <: Number], oldValue: Number, newValue: Number): Unit = {
        AnchorPane.setTopAnchor(self, 0)
        if (!_isDisplayed) {
          self.translateY.setValue(-self.height.value)
        }
      }
    })
  }

  override def bindScale(windowScaleX: DoubleBinding, windowScaleY: DoubleBinding): Unit = {
    contentGroup.scaleX.bind(windowScaleX)
    contentGroup.scaleY.bind(windowScaleY)
  }

  override def bindPrefSize(sceneWidth: ReadOnlyDoubleProperty, sceneHeight: ReadOnlyDoubleProperty): Unit = {
    prefHeight.bind(sceneHeight.divide(6))
    prefWidth.bind(sceneWidth.divide(15))
  }


  override def init(sceneWidth: ReadOnlyDoubleProperty, sceneHeight: ReadOnlyDoubleProperty): Unit = {
    AnchorPane.setLeftAnchor(self, sceneWidth.doubleValue() * .3)
    AnchorPane.setRightAnchor(self, sceneWidth.doubleValue() * .3)
    AnchorPane.setTopAnchor(self, 0)
    self.translateY.setValue(-self.height.value)
  }

  override def onPhaseChange(oldPhase: Phase, newPhase: Phase): () => Unit = {

    def fromAttackingToBattle = {val first = oldPhase match {case Attacking(_, _) => true case _ => false}
      val second = newPhase match {case Battle(_, _, _, _) => true case _ => false}
      first and second
    }

    def fromBattleToTransfer = {
      val first = oldPhase match {case Battle(_, _, _, false) => true case _ => false}
      val second = newPhase match {case Battle(_, _, _, true) => true case _ => false}
      first and second
    }

    def stillInBattleAfterRoll = {
      val first = oldPhase match {case Battle(_, _, _, false) => true case _ => false}
      val second = newPhase match {case Battle(_, _, Some(_), false) => true case _ => false}
      first and second
    }

    def fromBattleToAttack = {
      val first = oldPhase match {case Battle(_, _, _, false) => true case _ => false}
      val second = newPhase match {case Attacking(_, _) => true case _ => false}
      first and second
    }

    Unit match {
      case _ if fromAttackingToBattle => () => {
        openAnimation.setDelay(delayForAttCtrl())
        setStartingDice()
        updateColor()
        disableButtons()
        openAnim(() => enableButtons())
      }


      case _ if fromBattleToTransfer => () => {
        disableButtons()
        closeAnim(() => {})
      }

      case _ if stillInBattleAfterRoll => () => {
        displayBattleResults(_phase.value.asInstanceOf[Battle].previousBattle.get)
        enableButtons()
      }

      case _ if fromBattleToAttack => () => {
        disableButtons()
        closeAnim(() => {})
      }

      case _ =>
        () => {}
    }
  }
}
