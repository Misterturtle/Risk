package GUI

import javafx.beans.binding.DoubleBinding
import javafx.beans.property.ReadOnlyDoubleProperty
import javafx.beans.value.{ObservableValue, ChangeListener}
import javafx.geometry.Insets
import javafx.scene.layout._

import Service._

import scalafx.animation.TranslateTransition
import scalafx.geometry.Pos
import scalafx.scene.Group
import scalafx.scene.layout.{StackPane, AnchorPane, VBox}
import scalafx.scene.text.Text
import scalafx.util.Duration

import scalaz.Scalaz._
import Sugar.CustomSugar._

/**
  * Created by Harambe on 8/8/2017.
  */
class CardClaimDisplay(delayFromEndTurnCtrl: () => Duration, cardColDisp:CardCollectionDisplay) extends VBox with Scaleable with PlayerListener with PhaseListener with DeckListener with UIController {

  val self = this

  private var _isDisplayed = false
  def isDisplayed = _isDisplayed
  private val openAnimation = new TranslateTransition(new Duration(270), this)
  private val closeAnimation = new TranslateTransition(new Duration(270), this)

  val text = new Text("Shows Cards")
  text.setScaleX(2)
  text.setScaleY(2)
  text.styleClass.add("defaultText")
  val textContainer = new Group(text)

  val contentGroup = new VBox()
  contentGroup.alignment = Pos.Center
  contentGroup.children.addAll(textContainer)

  this.children.addAll(new Group(contentGroup))
  this.alignment = Pos.Center

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
    closeAnimation.setToY(this.height.get)
    closeAnimation.playFromStart()
  }

  def updateColor(): Unit ={
    val bg = new Background(new BackgroundFill(_player.value.color, new CornerRadii(10), Insets.EMPTY))
    this.setBackground(bg)
  }

  private def enableShowCardsOnAction(): Unit = {
    this.setOnMouseClicked(() => {
      disableButtons()
      closeAnim(() => {
        openCardDisplay()
        text.setText("Hide Cards")
        openAnim(()=>{
          enableHideCardsOnAction()
        })
      })
    })
  }

  private def enableHideCardsOnAction(): Unit = {
    this.setOnMouseClicked(() => {
      disableButtons()
      closeAnim(() => {
        cardColDisp.closeAnim(()=>{})
        text.setText("Show Cards")
        openAnim(()=>{
          enableShowCardsOnAction()
        })
      })
    })
  }

  def disableButtons(): Unit = {
    this.setOnMouseClicked(() => {})
    cardColDisp.disableButtons()
  }

  def openCardDisplay(): Unit ={
    cardColDisp.openAnim(() =>{
      cardColDisp.enableButtons()
    })
  }

  def anchorResize(sceneWidth: ReadOnlyDoubleProperty, sceneHeight: ReadOnlyDoubleProperty): Unit = {
    sceneWidth.addListener(new ChangeListener[Number] {
      override def changed(observable: ObservableValue[_ <: Number], oldValue: Number, newValue: Number): Unit = {
        AnchorPane.setLeftAnchor(self, newValue.doubleValue() * .45)

      }
    })

    sceneHeight.addListener(new ChangeListener[Number] {
      override def changed(observable: ObservableValue[_ <: Number], oldValue: Number, newValue: Number): Unit = {
        AnchorPane.setBottomAnchor(self, 0)
        if (!_isDisplayed) {
          self.translateY.setValue(self.height.value)
        }
      }
    })
  }

  def bindScale(windowScaleX: DoubleBinding, windowScaleY: DoubleBinding): Unit = {
    contentGroup.scaleX.bind(windowScaleX)
    contentGroup.scaleY.bind(windowScaleY)
  }

  def bindPrefSize(sceneWidth: ReadOnlyDoubleProperty, sceneHeight: ReadOnlyDoubleProperty): Unit = {
    prefHeight.bind(sceneHeight.divide(20))
    prefWidth.bind(sceneWidth.divide(8))
  }


  def init(sceneWidth: ReadOnlyDoubleProperty, sceneHeight: ReadOnlyDoubleProperty): Unit = {
    AnchorPane.setLeftAnchor(self, sceneWidth.doubleValue() * .45)
    AnchorPane.setBottomAnchor(self, 0)
    self.translateY.setValue(self.height.value)
  }

  def onPhaseChange(oldPhase: Phase, newPhase: Phase): () => Unit = {

    def has5Cards = _player.value.cards.size == 5

    def fromAnyToTurnPlacement:Boolean = {
      val first = oldPhase match {case x if x != TurnPlacement => true case _=> false}
      val second = newPhase match {case TurnPlacement=> true case _=> false}
      first and second
    }

    def toAnyFromTurnPlacement:Boolean = {

      val first = oldPhase match {case TurnPlacement => true case _=> false}
      val second = newPhase match {case x if x != TurnPlacement=> true case _=> false}
      first and second
    }


    Unit match {
      case _ if has5Cards && fromAnyToTurnPlacement => () => {
        forceCardSelection()
      }

      case _ if fromAnyToTurnPlacement => () => {
        updateColor()
        text.setText("Show Cards")
        openAnim(()=>enableShowCardsOnAction())
      }

      case _ if toAnyFromTurnPlacement => () => {
        disableButtons()
        cardColDisp.closeAnim(()=>{
          cardColDisp.reset()
        })
        closeAnim(() => {})
      }

      case _=> () => {}
    }
  }

  def onPlayerChange(oldPlayer: Player, newPlayer: Player): () => Unit = () => {
    if(oldPlayer != null && oldPlayer.cards.size == 5 && newPlayer.cards.size == 2){
      updateColor()
      text.setText("Hide Cards")
      openAnim(()=> {enableHideCardsOnAction()})
    }
  }

  def forceCardSelection(): Unit ={
    closeAnim(()=>{})
    openCardDisplay()
  }

  override def onDeckChange(oldDeck: DeckState, newDeck: DeckState): () => Unit = () =>{
    cardColDisp.updateTradeValue(newDeck.armiesToAward)
  }
}

