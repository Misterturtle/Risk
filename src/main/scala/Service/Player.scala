package Service

import javafx.scene.paint.{Paint, Color}

/**
  * Created by Harambe on 7/10/2017.
  */

trait Player{
  val playerNumber:Int
  val armies:Int
  val color: Paint
  val name: String
  val cards: List[Card]

  def awardCard(card:Card):Player= {
    if(isCountryTaken){
      this match {
        case h:HumanPlayer => h.copy(cards = card :: cards).setCountryTaken(false)
        case c:ComputerPlayer => c.copy(cards = card :: cards).setCountryTaken(false)
      }
    }
    else this
  }

  def removeCards(cardsToRemove:List[Card]):Player = {
    this match {
      case h:HumanPlayer => h.copy(cards = cards diff cardsToRemove)
      case c:ComputerPlayer => c.copy(cards = cards diff cardsToRemove)
    }
  }

  def isCountryTaken : Boolean = {
    this match {
      case h: HumanPlayer => h.countryTaken
      case c: ComputerPlayer => c.countryTaken
    }
  }

  def setCountryTaken(value: Boolean): Player  =  {
    this match {
      case h: HumanPlayer => h.copy(countryTaken = value)
      case c: ComputerPlayer => c.copy(countryTaken = value)
    }
  }

  def addArmies(amount:Int): Player = {
    this match {
      case h: HumanPlayer => h.copy(armies = armies + amount)
      case c: ComputerPlayer => c.copy(armies = armies + amount)
    }
  }

  def removeArmies(amount:Int): Player = {
    this match {
      case h: HumanPlayer => h.copy(armies = armies - amount)
      case c: ComputerPlayer => c.copy(armies = armies - amount)
    }
  }
}

case class ComputerPlayer(name: String, playerNumber: Int, armies: Int, color: Paint, cards: List[Card], compAsyncDelay: CompAsyncDelay = new CompAsyncDelay, countryTaken:Boolean = false) extends Player

case class HumanPlayer(name: String, playerNumber: Int, armies: Int, color: Paint, cards: List[Card], countryTaken:Boolean = false) extends Player