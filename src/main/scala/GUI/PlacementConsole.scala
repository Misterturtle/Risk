package GUI

import javafx.animation.Animation
import javafx.beans.binding.{DoubleBinding}
import javafx.beans.property.{ReadOnlyDoubleProperty}
import javafx.beans.value.{ObservableValue, ChangeListener}
import javafx.event.{ActionEvent, EventHandler}
import javafx.scene.layout.{CornerRadii, BackgroundFill, Background}

import Service._

import scalafx.animation.TranslateTransition
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.{Scene, Group}
import scalafx.scene.layout.{AnchorPane, VBox}
import scalafx.scene.text.Text
import scalafx.util.Duration
import scalaz.Scalaz._

import Sugar.CustomSugar._

/**
  * Created by Harambe on 8/6/2017.
  */

class PlacementConsole() extends VBox with Scaleable with PlayerListener with CountryListener with PhaseListener with UIController {


  private val self = this

  private var _isDisplayed = false
  def isDisplayed = _isDisplayed

  private val openAnimation = new TranslateTransition(new Duration(270), this)
  private val closeAnimation = new TranslateTransition(new Duration(270), this)
  def getTimeToFinish: Duration = (closeAnimation.currentTime.value.toMillis == closeAnimation.duration.value.toMillis) ? new Duration(closeAnimation.duration.value) | new Duration(closeAnimation.duration.value.subtract(closeAnimation.currentTime.value))


  private var allCountriesAreOwned = false


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

  private val contentGroup: Group = new Group(phaseBox)


  private val layoutBox = new VBox()
  layoutBox.children.addAll(new Group(contentGroup))
  layoutBox.alignment = Pos.Center
  val bg = new Background(new BackgroundFill(CustomColors.gray, new CornerRadii(0, 0, 100, 100, false), javafx.geometry.Insets.EMPTY))
  layoutBox.setBackground(bg)

  this.mouseTransparent = true
  this.children.addAll(layoutBox)
  this.alignment = Pos.TopCenter

  private def openAnim(onFinish: () => Unit): Unit = {
    openAnimation.setOnFinished(() => onFinish())
    _isDisplayed = true
    openAnimation.setToY(0)
    this.toFront()
    openAnimation.playFromStart()
  }

  private def closeAnim(onFinish: () => Unit): Unit = {
    closeAnimation.onFinished = new EventHandler[ActionEvent] {
      override def handle(event: ActionEvent): Unit = onFinish()
    }
    _isDisplayed = false
    closeAnimation.setToY(-this.height.get)
    closeAnimation.playFromStart()
  }

  private def updateAndOpenWhenClosed(): Unit = {
    updateColor()
    updateText()
    openAnim(() => {})
  }

  private def updateColor(): Unit = {
    val bg = new Background(new BackgroundFill(_player.value.color, new CornerRadii(0, 0, 100, 100, false), javafx.geometry.Insets.EMPTY))
    layoutBox.setBackground(bg)
  }

  private def updateText(): Unit = {
    _phase.value match {
      case InitialPlacement =>
        if (!allCountriesAreOwned) {
          phaseText.setText("Country Grab")
          hintText.setText("Claim an unowned country!")
        }
        else {
          phaseText.setText("Initial Placement")
          hintText.setText("Place an army on an owned country!")
        }

      case TurnPlacement =>
        phaseText.setText("Turn Placement")
        hintText.setText("Place all awarded armies!")

      case _ =>
    }
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
    contentGroup.scaleX.bind(windowScaleX)
    contentGroup.scaleY.bind(windowScaleY)
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


  override def onPlayerChange(oldPlayer: Player, newPlayer: Player): () => Unit = () => {
    _phase.value match {
      case InitialPlacement | TurnPlacement =>
        openAnimation.stop()
        closeAnim(updateAndOpenWhenClosed)

      case _ =>
        if (_isDisplayed) {
          closeAnim(() => {})
        }
    }
  }


  override def onCountryChange(oldCountries: List[Country], newCountries: List[Country]): () => Unit = { () => {
    if (_countries.value.forall(_.owner.nonEmpty)) {
      allCountriesAreOwned = true
    } else {
      allCountriesAreOwned = false
    }
  }
  }

  override def onPhaseChange(oldPhase: Phase, newPhase: Phase): () => Unit = { () => {
    _phase.value match {
      case InitialPlacement | TurnPlacement =>
        openAnimation.stop()
        closeAnim(updateAndOpenWhenClosed)

      case _ =>
        if(_isDisplayed){
          openAnimation.stop()
          closeAnim(() => {})
        }
        if (closeAnimation.status.value == Animation.Status.RUNNING) {
          closeAnimation.stop()
          closeAnim(() => {})
        }
    }
  }
  }
}
