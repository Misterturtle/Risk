package GUI

import javafx.beans.binding.DoubleBinding
import javafx.beans.property.ReadOnlyDoubleProperty
import javafx.beans.value.{ObservableValue, ChangeListener}
import javafx.geometry.Insets
import javafx.scene.layout.{CornerRadii, Background, BackgroundFill}
import javafx.scene.paint.{Color, Paint}

import Service._

import scalafx.animation.TranslateTransition
import scalafx.geometry.Pos
import scalafx.scene.Group
import scalafx.scene.layout._
import scalafx.scene.text.Text
import scalafx.util.Duration

import Sugar.CustomSugar._
import scalaz.Scalaz._

/**
  * Created by Harambe on 8/26/2017.
  */
class CardCollectionDisplay(messageService:(Input) => Unit, enableWMInteractions: () => Unit, disableWMInteractions: () => Unit) extends VBox with Scaleable with UIController{

  private val self = this
  private val cardSlots = Array(new CardSlot(reportClicked), new CardSlot(reportClicked), new CardSlot(reportClicked), new CardSlot(reportClicked), new CardSlot(reportClicked))
  private val cardSlotsContainer = new HBox()
  private val tradeText = new Text()
  tradeText.styleClass.add("defaultText")
  tradeText.scaleX = 2
  tradeText.scaleY = 2
  private val tradeBox = new StackPane()
  tradeBox.setBackground(new Background(new BackgroundFill(Color.color(0,0,0,.5), new CornerRadii(10), Insets.EMPTY)))
  private val tradeShadeBox = new HBox()
  tradeShadeBox.setBackground(new Background(new BackgroundFill(Color.color(0,0,0,.5), new CornerRadii(10), Insets.EMPTY)))

  tradeShadeBox.prefHeight.bind(tradeBox.prefHeight)
  tradeShadeBox.prefWidth.bind(tradeBox.maxWidth)
  tradeBox.maxWidth.bind(this.prefWidth.delegate.divide(3))
  tradeBox.prefHeight.bind(this.prefHeight.delegate.divide(4))

  tradeBox.setBackground(new Background(new BackgroundFill(CustomColors.gray, new CornerRadii(10), Insets.EMPTY)))
  tradeBox.alignment = Pos.Center
  tradeBox.children.addAll(tradeText, tradeShadeBox)
  tradeShadeBox.toFront()

  private var _isDisplayed = false
  def isDisplayed = _isDisplayed
  private val openAnimation = new TranslateTransition(new Duration(270), this)
  private val closeAnimation = new TranslateTransition(new Duration(270), this)

  cardSlots.foreach(cs => cardSlotsContainer.children.add(cs))
  cardSlots.foreach(cs => setCardSlotScaling(cs))

  this.alignment = Pos.Center
  this.spacing.bind(prefWidth.delegate.multiply(.025))
  this.children.addAll(cardSlotsContainer, tradeBox)

  def setCardSlotScaling(cs:CardSlot): Unit = {
    cs.prefHeight.bind(this.prefHeight.delegate.multiply(.8))
    cs.prefWidth.bind(this.prefWidth.delegate.multiply(.15))
    cs.maxHeight.bind(this.prefHeight.delegate.multiply(.8))
    cs.maxWidth.bind(this.prefWidth.delegate.multiply(.15))
    cs.minHeight.bind(this.prefHeight.delegate.multiply(.8))
    cs.minWidth.bind(this.prefWidth.delegate.multiply(.15))
    cs.vgrow = Priority.Always
    cs.hgrow = Priority.Always
  }

  def openAnim(onFinish: () => Unit): Unit = {
    disableWMInteractions()
    openAnimation.setOnFinished(onFinish)
    _isDisplayed = true
    openAnimation.setToY(0)
    openAnimation.playFromStart()
  }

  def closeAnim(onFinish: () => Unit): Unit = {
    enableWMInteractions()
    closeAnimation.setOnFinished(onFinish)
    _isDisplayed = false
    closeAnimation.setToY(this.scene.value.getHeight - AnchorPane.getTopAnchor(this))
    closeAnimation.playFromStart()
  }


  def enableButtons(): Unit ={
    cardSlots.foreach(_.enableButtons())
  }

  def disableButtons(): Unit ={
    cardSlots.foreach(_.disableButtons())
  }

  def checkIfValidTrade(): Boolean ={
    val selected = cardSlots.filter(_.isSelected)
    def isSameType = selected.count(_.cardUI.get.card.armyType == selected.head.cardUI.get.card.armyType) == 3
    def isAllDifferent = (selected.count(_.cardUI.get.card.armyType == Infantry) == 1) && (selected.count(_.cardUI.get.card.armyType == Artillery) == 1) && (selected.count(_.cardUI.get.card.armyType == Cavalry) == 1)

    if(selected.size == 3){
      isSameType || isAllDifferent
    } else false
  }

  def enableTrade(): Unit = {
      tradeShadeBox.visible = false
      val selectedCards = cardSlots.filter(_.isSelected).map(_.cardUI.get.card).toList
      tradeBox.setOnMouseClicked(()=>{
        messageService(TurnInCards(selectedCards))
        reset()
    })
  }

  def reset(): Unit ={
    disableTrade()
    cardSlots.foreach(_.deselect())
  }

  def disableTrade(): Unit ={

    tradeShadeBox.visible = true
    tradeBox.setOnMouseClicked(()=>{})
  }

  def reportClicked: () => Unit = () => {
    checkIfValidTrade() ? enableTrade() | disableTrade()
  }

  def updatePlayer(newPlayer:Player): Unit = {
    updateColor(newPlayer.color)
    updateCardSlots(newPlayer.cards)

  }

  def updateTradeValue(newValue:Int): Unit ={
    tradeText.setText("Trade for "+newValue+" armies")
  }

  private def updateColor(color:Paint): Unit ={
    val bg = new Background(new BackgroundFill(color, new CornerRadii(10), Insets.EMPTY))
    tradeBox.setBackground(bg)

    cardSlotsContainer.setBackground(bg)
  }

  private def updateCardSlots(cards:List[Card]): Unit ={
    for(a<-0 to 4){
      if(cardSlots(a).cardUI.nonEmpty){
        cardSlots(a).deselect()
      }
      cards.isDefinedAt(a) ? cardSlots(a).update(Some(cards(a))) | cardSlots(a).update(None)
    }
  }


  override def anchorResize(sceneWidth: ReadOnlyDoubleProperty, sceneHeight: ReadOnlyDoubleProperty): Unit = {
    sceneWidth.addListener(new ChangeListener[Number] {
      override def changed(observable: ObservableValue[_ <: Number], oldValue: Number, newValue: Number): Unit = {
        AnchorPane.setLeftAnchor(self, newValue.doubleValue() * .11)
      }
    })

    sceneHeight.addListener(new ChangeListener[Number] {
      override def changed(observable: ObservableValue[_ <: Number], oldValue: Number, newValue: Number): Unit = {
        AnchorPane.setTopAnchor(self, sceneHeight.doubleValue() * .25)
        if (!_isDisplayed) {
          self.translateY.setValue(sceneHeight.doubleValue() - AnchorPane.getTopAnchor(self))
        }
      }
    })
  }

  override def bindScale(windowScaleX: DoubleBinding, windowScaleY: DoubleBinding): Unit = {
    scaleX.bind(windowScaleX)
    scaleY.bind(windowScaleY)
  }

  override def bindPrefSize(sceneWidth: ReadOnlyDoubleProperty, sceneHeight: ReadOnlyDoubleProperty): Unit = {
    prefHeight.bind(sceneHeight.divide(2))
    prefWidth.bind(sceneWidth.multiply(.8))
  }

  override def init(sceneWidth: ReadOnlyDoubleProperty, sceneHeight: ReadOnlyDoubleProperty): Unit = {
    AnchorPane.setLeftAnchor(self, sceneWidth.doubleValue() * .11)
    AnchorPane.setTopAnchor(self, sceneHeight.doubleValue() * .25)
    self.translateY.setValue(sceneHeight.doubleValue() - AnchorPane.getTopAnchor(this))
  }
}
