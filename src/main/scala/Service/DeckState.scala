package Service

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * Created by Harambe on 8/28/2017.
  */

object DeckState{

  def apply(): DeckState = {
    var cards:List[Card] = Nil
    val countries = CountryFactory.getCountries.map(_.name)
    val armyTypeMap = Map[Int, ArmyType](1 -> Infantry, 2 -> Cavalry, 3 -> Artillery)
    for(a<-0 until 14){
      val index = a * 3
      cards = new Card(countries(index), armyTypeMap(1)) :: new Card(countries(index + 1), armyTypeMap(2)) :: new Card(countries(index + 2), armyTypeMap(3)) :: cards
    }
    new DeckState(cards, rGen = new RandomFactory)
  }

  def apply(mockRGen: RandomFactory): DeckState = {
    var cards:List[Card] = Nil
    val countries = CountryFactory.getCountries.map(_.name)
    val armyTypeMap = Map(1 -> Infantry, 2 -> Cavalry, 3 -> Artillery)
    for(a<-0 until 14){
      val index = a * 3
      cards = new Card(countries(index), armyTypeMap(1)) :: new Card(countries(index + 1), armyTypeMap(2)) :: new Card(countries(index + 2), armyTypeMap(3)) :: cards
    }
    new DeckState(cards, rGen = mockRGen)
  }
}

case class DeckState(cards: List[Card], armiesToAward: Int = 4, rGen: RandomFactory) {

  def draw(): (DeckState, Card) ={
    val card = cards(rGen.cardIndex(cards.size))
    (this.copy(cards = cards diff List(card)), card)
  }

  def increaseArmiesToAward: DeckState = copy( armiesToAward =
    armiesToAward match {
      case _ if armiesToAward < 12 => armiesToAward + 2
      case _ if armiesToAward == 12 => armiesToAward + 3
      case _ if armiesToAward > 12 => armiesToAward + 5
    }
  )

  def add(newCards:Card *): DeckState = {
    copy(cards = newCards.toList ::: cards)
  }
}
