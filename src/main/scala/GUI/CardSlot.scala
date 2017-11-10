package GUI

import javafx.geometry.Insets
import javafx.scene.layout.{CornerRadii, BackgroundFill, Background}
import javafx.scene.paint.Color

import Service.{ArmyType, Card}

import scalafx.scene.layout.VBox

import Sugar.CustomSugar._

/**
  * Created by Harambe on 8/26/2017.
  */
class CardSlot(reportClicked: () => Unit) extends VBox {

  private var _isSelected = false
  def isSelected = _isSelected
  def setIsSelected(value:Boolean) = _isSelected = value
  private var _cardUI:Option[CardUI] = None
  def cardUI = _cardUI

  val bgShade = new Background(new BackgroundFill(Color.color(0,0,0, .3), new CornerRadii(10), Insets.EMPTY))
  val whiteBG = new Background(new BackgroundFill(Color.color(1,1,1, 1), new CornerRadii(10), Insets.EMPTY))

  val cardContainer = new VBox()

  this.children.add(cardContainer)

  def update(newCard:Option[Card]): Unit ={

    newCard match {
      case Some(c:Card) =>
        val newCardUI = new CardUI(c)
        _cardUI = Some(newCardUI)
        newCardUI.prefHeight.bind(this.prefHeight)
        newCardUI.prefWidth.bind(this.prefWidth)
        newCardUI.maxHeight.bind(this.maxHeight)
        newCardUI.maxWidth.bind(this.maxWidth)
        newCardUI.minHeight.bind(this.minHeight)
        newCardUI.minWidth.bind(this.minWidth)
        newCardUI.init()

        cardContainer.children.removeAll(cardContainer.getChildrenUnmodifiable)
        cardContainer.children.add(newCardUI)
        enableButtons()
        this.visible = true

      case None =>
        disableButtons()
        this.visible = false
    }
  }


  def disableButtons(): Unit ={
    this.setOnMouseClicked(()=>{})
  }

  def enableButtons(): Unit ={
    this.setOnMouseClicked(()=>{
      toggleSelection()
      reportClicked()
    })
  }

  private def toggleSelection(): Unit ={
    if(_isSelected)
      deselect()
    else select()


  }

  def deselect(): Unit ={
    _isSelected = false
    cardContainer.setBackground(whiteBG)
    if(cardUI.nonEmpty)
      cardUI.get.hideShadeCard()
  }

  def select(): Unit ={
    _isSelected = true
    cardContainer.setBackground(bgShade)
    if(cardUI.nonEmpty)
      cardUI.get.shadeCard()
  }
}
