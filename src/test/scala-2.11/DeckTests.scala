import javafx.embed.swing.JFXPanel

import GUI.CustomColors
import Service._
import org.mockito.ArgumentMatchers
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FreeSpec, Matchers}

import org.mockito.Mockito._

import scala.collection.mutable

/**
  * Created by Harambe on 8/28/2017.
  */
class DeckTests extends FreeSpec with Matchers with MockitoSugar {

  //Init graphics
  new JFXPanel()

  "A deck should be created" - {
    val deck = DeckState()

    "With 42 cards" in {
      deck.cards.size shouldBe 42
    }

    "With 14 of each Army Type" in {
      var infantry = 0
      var cavalry = 0
      var artillery = 0
      val typeMap = Map[ArmyType, ()=>Unit](Infantry -> (() => infantry += 1), Cavalry -> (() => cavalry += 1), Artillery -> (() => artillery += 1))

      deck.cards.foreach(c => typeMap(c.armyType)())

      infantry shouldBe 14
      cavalry shouldBe 14
      artillery shouldBe 14
    }

    "Contain only one of each country" in {
      for(a<- 0 until 42){
        val uniqueCountry = deck.cards.filter(_.countryName == deck.cards(a).countryName)
        uniqueCountry.length shouldBe 1
      }
    }

    "Contain all 42 countries" in {
      val countries = CountryFactory.getCountries.map(_.name)

      countries.length shouldBe 42
      countries.foreach{c =>
        deck.cards.exists(_.countryName == c) shouldBe true
      }
    }
  }

  "A deck should be able to draw a random card" in {


    val rGen = mock[RandomFactory]
    when(rGen.cardIndex(42)).thenReturn(20)
    when(rGen.cardIndex(41)).thenReturn(4)
    when(rGen.cardIndex(40)).thenReturn(9)

    val deck = DeckState(rGen)
    val (ds, card) = deck.draw()
    val (ds2, card2) = ds.draw()
    val (ds3, card3) = ds2.draw()

    val expectedCards = List(deck.cards(20), deck.cards(4), deck.cards(10))

    List(card, card2,card3) shouldBe expectedCards
    ds3.cards.size shouldBe 39
  }

  "The deck should be able to add cards to the deck" in {
    val deck = DeckState()
    val card = new Card("Alaska", Infantry)
    val card2 = new Card("Alberta", Cavalry)
    val card3 = new Card("Ontario", Infantry)
    val newDeck = deck.add(card, card2, card3)
    newDeck.cards.count(_.countryName == card.countryName) shouldBe 2
    newDeck.cards.count(_.countryName == card2.countryName) shouldBe 2
    newDeck.cards.count(_.countryName == card3.countryName) shouldBe 2
  }
}
