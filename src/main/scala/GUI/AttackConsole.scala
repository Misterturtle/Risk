package GUI

import javafx.beans.binding.DoubleBinding
import javafx.beans.property
import javafx.beans.property.{ReadOnlyDoubleProperty, SimpleDoubleProperty, SimpleStringProperty}
import javafx.beans.value.{ObservableValue, ChangeListener}
import javafx.event.{ActionEvent, EventHandler}
import javafx.scene.layout.{CornerRadii, BackgroundFill, Background}
import javafx.scene.paint.Color

import Service._

import scalafx.animation.TranslateTransition
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Group
import scalafx.scene.layout.{StackPane, AnchorPane, VBox, HBox}
import scalafx.scene.text.Text
import scalafx.util.Duration

import scalaz.Scalaz._

import Sugar.CustomSugar._

/**
  * Created by Harambe on 8/6/2017.
  */
class AttackConsole(delayForPlacementCtrl: () => Duration, delayForBattleCtrl: () => Duration) extends VBox with Scaleable with PhaseListener with PlayerListener with UIController {
  private val self = this
  private var _isDisplayed = false
  def isDisplayed = _isDisplayed
  private val openAnimation = new TranslateTransition(new Duration(270), this)
  private val closeAnimation = new TranslateTransition(new Duration(270), this)
  def getTimeToFinish: Duration = (closeAnimation.currentTime.value.toMillis == closeAnimation.duration.value.toMillis) ? new Duration(closeAnimation.duration.value) | new Duration(closeAnimation.duration.value.subtract(closeAnimation.currentTime.value))

  private val phaseText = new Text()
  phaseText.setScaleX(3)
  phaseText.setScaleY(3)
  phaseText.styleClass.add("defaultText")

  private val hintText = new Text()
  hintText.setScaleX(1.5)
  hintText.setScaleY(1.5)
  hintText.styleClass.add("defaultText")

  private val phaseBox = new VBox()
  phaseBox.alignment = Pos.Center
  phaseBox.children.addAll(new Group(phaseText), new Group(hintText))
  phaseBox.padding = Insets(0, 20, 10, 20)
  phaseBox.spacing = 10

  private val content: VBox = new VBox()
  content.children.addAll(phaseBox)
  content.alignment = Pos.Center

  val bgBox = new VBox()
  bgBox.alignment = Pos.Center
  bgBox.children.add(new Group(content))

  val bg = new Background(new BackgroundFill(CustomColors.gray, new CornerRadii(0, 0, 100, 100, false), javafx.geometry.Insets.EMPTY))
  bgBox.setBackground(bg)


  this.mouseTransparent = true
  this.children.addAll(bgBox)
  this.alignment = Pos.TopCenter

  private def updateColor(): Unit ={
    val bg = new Background(new BackgroundFill(_player.value.color, new CornerRadii(0, 0, 100, 100, false), javafx.geometry.Insets.EMPTY))
    bgBox.setBackground(bg)
  }

  private def updateText(phase: String, hint: String): Unit ={
    phaseText.setText(phase)
    hintText.setText(hint)
  }

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

  override def anchorResize(sceneWidth: ReadOnlyDoubleProperty, sceneHeight: ReadOnlyDoubleProperty): Unit = {
    sceneWidth.addListener(new ChangeListener[Number] {
      override def changed(observable: ObservableValue[_ <: Number], oldValue: Number, newValue: Number): Unit = {
        AnchorPane.setLeftAnchor(self, newValue.doubleValue() * .3)
        AnchorPane.setRightAnchor(self, newValue.doubleValue() * .3)
      }
    })

    sceneHeight.addListener(new ChangeListener[Number] {
      override def changed(observable: ObservableValue[_ <: Number], oldValue: Number, newValue: Number): Unit = {
        if (!_isDisplayed) {
          self.translateY.setValue(-self.height.value)
        }
      }
    })
  }

  override def bindScale(windowScaleX: DoubleBinding, windowScaleY: DoubleBinding): Unit = {
    content.scaleX.bind(windowScaleX)
    content.scaleY.bind(windowScaleY)
  }

  override def bindPrefSize(sceneWidth: ReadOnlyDoubleProperty, sceneHeight: ReadOnlyDoubleProperty): Unit = {
    prefHeight.bind(sceneHeight.divide(4))
    prefWidth.bind(sceneWidth.divide(10))
  }


  override def init(sceneWidth: ReadOnlyDoubleProperty, sceneHeight: ReadOnlyDoubleProperty): Unit = {
    AnchorPane.setLeftAnchor(self, sceneWidth.doubleValue() * .3)
    AnchorPane.setRightAnchor(self, sceneWidth.doubleValue() * .3)
    self.translateY.setValue(-self.height.value)
  }

  override def onPhaseChange(oldPhase: Phase, newPhase: Phase): () => Unit = {

    def fromPlacement:Boolean = {
      val first = oldPhase == TurnPlacement
      val second = newPhase match {case Attacking(None, _) => true case _ => false}
      first and second
    }
    def toSourceSelected:Boolean = {
      val first = oldPhase match {case Attacking(None, _) => true case _ => false}
      val second = newPhase match {case Attacking(Some(_),_) => true case _=> false}
      first and second
    }
    def toNoSource:Boolean = {
      val first = oldPhase match {case Attacking(Some(_), _) => true case _ => false}
      val second = newPhase match {case Attacking(None, _) => true case _ => false}
      first and second
    }
    def toBattle:Boolean = {
      val first = oldPhase match {case Attacking(Some(_), _) => true case _ => false}
      val second = newPhase match {case Battle(_,_,_,_) => true case _ => false}
      first and second
    }
    def fromBattle:Boolean = {
      val first = oldPhase match {case Battle(_,_,_,_) => true case _ => false}
      val second = newPhase match {case Attacking(None, _) => true case _ => false}
      first and second
    }
    def fromTransfer:Boolean = {
      val first = oldPhase match {case Battle(_,_,_,trans) if trans => true case _=> false}
      val second = newPhase match {case Attacking(None,_) => true case _=> false}
      first and second
    }
    def toTransfer:Boolean = {
      val first = oldPhase match {case Attacking(_,_) => true case _=> false}
      val second  = newPhase match {case Reinforcement(_,_) => true case _=> false}
      first and second
    }

    true match {
      case _ if fromPlacement => () => {
        updateColor()
        updateText("Attack Phase", "Choose a country to attack from!")
        openAnimation.setDelay(delayForPlacementCtrl())
        openAnim(() => openAnimation.setDelay(new Duration(0)))}


      case _ if toSourceSelected => () => {
        closeAnim(() => {
          updateText("Attack Phase", "Invade a surrounding enemy country!")
          openAnim(() => {})
        })}


      case _ if toNoSource => () => {
        closeAnim(() => {
          updateText("Attack Phase", "Choose a country to attack from!")
          openAnim(() => {})
        })
      }

      case _ if toBattle => () => {
        closeAnim(() => {})}


      case _ if fromBattle => () => {
        openAnimation.setDelay(delayForBattleCtrl())
        updateText("Attack Phase", "Choose a country to attack from!")
        openAnim(()=>{openAnimation.setDelay(new Duration(0))})}

      case _ if fromTransfer => () => {
        updateText("Attack Phase", "Choose a country to attack from!")
        openAnim(()=>{})}

      case _ if toTransfer => () => {
        closeAnim(()=>{})
      }

      case _ => () => {}
    }
  }

  override def onPlayerChange(oldPlayer: Player, newPlayer: Player): () => Unit = () => {}



}
