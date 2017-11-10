import javafx.embed.swing.JFXPanel

import GUI.{CustomColors, WorldMapUI}
import Service._
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FreeSpec, Matchers}
import scalaz._
import Scalaz._


/**
  * Created by Harambe on 7/19/2017.
  */
class TurnPlacementTests extends FreeSpec with Matchers with MockitoSugar {

  //Init Graphics
  new JFXPanel()

  val players = List[Player](HumanPlayer("Turtle", 1, 0, CustomColors.red, Nil), HumanPlayer("Boy Wonder", 2, 0, CustomColors.blue, Nil), ComputerPlayer("Some Scrub", 3, 0, CustomColors.green, Nil))
  var mutableCountries = CountryFactory.getCountries
  var mutableWorldMap = new WorldMap(mutableCountries, players, 1, InitialPlacement)

  for (a <- 1 to 14) {
    for (b <- 0 until 3) {
      def unownedCountry = mutableWorldMap.countries.find(_.owner.isEmpty).get
      mutableWorldMap = mutableWorldMap.updateSingleCountry(unownedCountry.copy(armies = 1, owner = Some(players(b))))
    }
  }

  val wm = mutableWorldMap

  val player1Country = wm.countries.find(_.owner.map(_.playerNumber).contains(1)).get.name
  val player2Country = wm.countries.find(_.owner.map(_.playerNumber).contains(2)).get.name
  val player3Country = wm.countries.find(_.owner.map(_.playerNumber).contains(3)).get.name

  "Placing the last army in the InitPlacePhase should transition to Turn Placement Phase" - {
    val wm2 = wm
      .copy(activePlayerNumber = 3)
      .setPhase(InitialPlacement)
      .updatePlayer(wm.getPlayerByPlayerNumber(3).get.addArmies(1))
    val turnPlaceWM = Effects.getCountryClickedEffect(wm2, player3Country).eval(StateStamp(-1))

    "The phase should be set to TurnPlacement" in {
      turnPlaceWM.phase shouldBe TurnPlacement
    }

    "Set player 1 as the active player" in {
      turnPlaceWM.activePlayerNumber shouldBe 1
    }
  }

  "Armies should be allocated at the beginning of TurnPhase" - {
    "Every 3 countries owned allocates 1 army (No continents owned)" - {
      val wm2 = wm.copy(countries = CountryFactory.getCountries.map(_.copy(owner = wm.getPlayerByPlayerNumber(3))))
        .copy(activePlayerNumber = 3)
        .setPhase(Reinforcement(None,None))

      "9 owned" in {
        var _wm = wm2
        for (a <- 0 until 9) {
          val claimedCountry = _wm.getCountry(CountryFactory.continentLookup("Asia")(a)).setOwner(_wm.getPlayerByPlayerNumber(1).get)
          _wm = _wm.updateSingleCountry(claimedCountry)
        }
        _wm = Effects.endTurn(_wm).eval(StateStamp(-1))

        _wm.getPlayerByPlayerNumber(1).get.armies shouldBe 3
      }

      "8 owned" in {
        var _wm = wm2
        for (a <- 0 until 8) {
          val claimedCountry = _wm.getCountry(CountryFactory.continentLookup("Asia")(a)).setOwner(_wm.getPlayerByPlayerNumber(1).get)
          _wm = _wm.updateSingleCountry(claimedCountry)
        }
        _wm = Effects.endTurn(_wm).eval(StateStamp(-1))

        _wm.getPlayerByPlayerNumber(1).get.armies shouldBe 2
      }

      "11 owned" in {
        var _wm = wm2
        for (a <- 0 until 11) {
          val claimedCountry = _wm.getCountry(CountryFactory.continentLookup("Asia")(a)).setOwner(_wm.getPlayerByPlayerNumber(1).get)
          _wm = _wm.updateSingleCountry(claimedCountry)
        }
        _wm = Effects.endTurn(_wm).eval(StateStamp(-1))

        _wm.getPlayerByPlayerNumber(1).get.armies shouldBe 3
      }

      "12 owned" in {
        var _wm = wm2
        for (a <- 0 until 11) {
          val claimedCountry = _wm.getCountry(CountryFactory.continentLookup("Asia")(a)).setOwner(_wm.getPlayerByPlayerNumber(1).get)
          _wm = _wm.updateSingleCountry(claimedCountry)
        }
        val diffContCoun = _wm.getCountry(CountryFactory.continentLookup("Europe")(0)).setOwner(_wm.getPlayerByPlayerNumber(1).get)
        _wm = _wm.updateSingleCountry(diffContCoun)

        _wm = Effects.endTurn(_wm).eval(StateStamp(-1))

        _wm.getPlayerByPlayerNumber(1).get.armies shouldBe 4
      }

      "13 owned" in {
        var _wm = wm2
        for (a <- 0 until 11) {
          val claimedCountry = _wm.getCountry(CountryFactory.continentLookup("Asia")(a)).setOwner(_wm.getPlayerByPlayerNumber(1).get)
          _wm = _wm.updateSingleCountry(claimedCountry)
        }
        for (a <- 0 until 2) {
          val diffContCoun = _wm.getCountry(CountryFactory.continentLookup("Europe")(a)).setOwner(_wm.getPlayerByPlayerNumber(1).get)
          _wm = _wm.updateSingleCountry(diffContCoun)
        }

        _wm = Effects.endTurn(_wm).eval(StateStamp(-1))

        _wm.getPlayerByPlayerNumber(1).get.armies shouldBe 4
      }
    }

    "Owning an entire Continent should award bonus armies" - {

      val wm2 = wm.copy(countries = CountryFactory.getCountries.map(_.copy(owner = wm.getPlayerByPlayerNumber(3))))
        .copy(activePlayerNumber = 3)
        .setPhase(Reinforcement(None,None))

      "Owning North America should give 5 armies" in {
        var _wm = wm2

        //9 Countries in North America
        CountryFactory.continentLookup("North America").foreach(c=> _wm = _wm.updateSingleCountry(
          _wm.getCountry(c).copy(owner = _wm.getPlayerByPlayerNumber(1))
        ))
        _wm = Effects.endTurn(_wm).eval(StateStamp(-1))

        _wm.getPlayerByPlayerNumber(1).get.armies shouldBe 8
      }

      "Owning Asia should give 7 armies" in {
        var _wm = wm2
        //12 Countries in Asia
        CountryFactory.continentLookup("Asia").foreach(c=> _wm = _wm.updateSingleCountry(
          _wm.getCountry(c).copy(owner = _wm.getPlayerByPlayerNumber(1))
        ))
        _wm = Effects.endTurn(_wm).eval(StateStamp(-1))

        _wm.getPlayerByPlayerNumber(1).get.armies shouldBe 11
      }

      "Owning South America should give 2 armies" in {
        var _wm = wm2
        //4 Countries in South America
        CountryFactory.continentLookup("South America").foreach(c=> _wm = _wm.updateSingleCountry(
          _wm.getCountry(c).copy(owner = _wm.getPlayerByPlayerNumber(1))
        ))
        _wm = Effects.endTurn(_wm).eval(StateStamp(-1))

        _wm.getPlayerByPlayerNumber(1).get.armies shouldBe 3
      }

      "Owning Europe should give 5 armies" in {
        var _wm = wm2
        //7 Countries in Europe
        CountryFactory.continentLookup("Europe").foreach(c=> _wm = _wm.updateSingleCountry(
          _wm.getCountry(c).copy(owner = _wm.getPlayerByPlayerNumber(1))
        ))
        _wm = Effects.endTurn(_wm).eval(StateStamp(-1))

        _wm.getPlayerByPlayerNumber(1).get.armies shouldBe 7
      }

      "Owning Australia should give 2 armies" in {
        var _wm = wm2
        //4 Countries in Australia
        CountryFactory.continentLookup("Australia").foreach(c=> _wm = _wm.updateSingleCountry(
          _wm.getCountry(c).copy(owner = _wm.getPlayerByPlayerNumber(1))
        ))
        _wm = Effects.endTurn(_wm).eval(StateStamp(-1))

        _wm.getPlayerByPlayerNumber(1).get.armies shouldBe 3
      }

      "Owning Africa should give 3 armies" in {
        var _wm = wm2
        //6 Countries in Africa
        CountryFactory.continentLookup("Africa").foreach(c=> _wm = _wm.updateSingleCountry(
          _wm.getCountry(c).copy(owner = _wm.getPlayerByPlayerNumber(1))
        ))
        _wm = Effects.endTurn(_wm).eval(StateStamp(-1))

        _wm.getPlayerByPlayerNumber(1).get.armies shouldBe 5
      }
    }
  }


  "During a human player's Turn Placement Phase" - {


    "Clicking on an unowned country should do nothing" in {
      val unownedCountry = wm.countries
        .find(!_.owner.map(_.playerNumber).contains(wm.getActivePlayer.get.playerNumber))
        .get
      val newWM = Effects.getCountryClickedEffect(wm, unownedCountry.name).eval(StateStamp(-1))

      newWM shouldBe wm
    }

    "Clicking on an owned country should place an army" in {
      val ownedCountry = wm.countries.find(_.owner.map(_.playerNumber).contains(wm.getActivePlayer.get.playerNumber)).get
      val wm2 = wm
        .setActivePlayer(1)
        .updatePlayer(wm.getPlayerByPlayerNumber(1).get.addArmies(4))
        .setPhase(TurnPlacement)
      val newWM = Effects.getCountryClickedEffect(wm2, ownedCountry.name).eval(StateStamp(-1))

      newWM.getActivePlayer.get.armies shouldBe 3
      newWM.getCountry(ownedCountry.name).armies shouldBe ownedCountry.armies + 1
    }

    "Placing the last army should transition to the attack phase" in {
      val lastPlacementWM = wm
        .updatePlayer(wm.getPlayerByPlayerNumber(1).get.removeArmies(wm.getPlayerByPlayerNumber(1).get.armies))
        .setPhase(TurnPlacement)
        .setActivePlayer(1)
        .updatePlayer(wm.getPlayerByPlayerNumber(1).get.addArmies(1))

      val attackPhaseWM = Effects.getCountryClickedEffect(lastPlacementWM, lastPlacementWM.countries.find(_.owner.get.playerNumber == 1).get.name).eval(StateStamp(-1))

      attackPhaseWM.phase shouldBe Attacking(None, None)
    }
  }

  "During a computer player's Turn Placement" - {
    val compCountries = List("Alaska", "Alberta", "Siam", "North Africa", "Central America", "China")
    val endOfHumanTurn = wm
      .setActivePlayer(2)
      .setPhase(Reinforcement(None,None))
      .updateSomeCountries(CountryFactory.getCountries.map(_.setOwner(wm.getPlayerByPlayerNumber(2).get)))
      .updateSomeCountries(compCountries.map(wm.getCountry(_).setOwner(wm.getPlayerByPlayerNumber(3).get)))
    val wm2 = Effects.endTurn(endOfHumanTurn).eval(StateStamp(-1))

    "The computer should be awarded the correct armies" in {
      wm2.getPlayerByPlayerNumber(3).get.armies shouldBe endOfHumanTurn.getPlayerByPlayerNumber(3).get.armies + 2
    }

    "If the computer has 5 cards" - {
      val compCards = List(new Card("Quebec", Infantry), new Card("Argentina", Infantry), new Card("China", Infantry), new Card("Peru", Infantry), new Card("NW Territory", Artillery))
      val wm2 = endOfHumanTurn.updatePlayer(
        endOfHumanTurn
          .getPlayerByPlayerNumber(3).get
          .awardCard(compCards(0))
          .awardCard(compCards(1))
          .awardCard(compCards(2))
          .awardCard(compCards(3))
          .awardCard(compCards(4)))

      val result = Effects.endTurn(wm2).eval(StateStamp(-1))

      "The computer should turn in 3 cards" in {
        result.getPlayerByPlayerNumber(3).get.cards.size shouldBe 2
      }

      "Those cards should be added to the deck" in {
        result.deckState.cards.size shouldBe 45
      }
    }
  }

  "If the TurnInCards command is received" - {

    "If the cards are all 3 the same" - {
      val cards = List(new Card("Alaska", Infantry), new Card("Alberta", Infantry), new Card("Ontario", Infantry))
      val ownedCountries = List(("Alaska", true), ("Alberta", true), ("Ontario", false))
      val (mutWM, servInput, origArmies, origCards) = cardTestSetup(wm, cards, ownedCountries)
      servInput.receiveInput(TurnInCards(cards))

      "The cards should be removed from the player" in {
        mutWM.get.getActivePlayer.get.cards.size shouldBe 0
      }

      "The cards should be added back to the deck" in {
        origCards.count(_.countryName == "Alaska") shouldBe 0
        origCards.count(_.countryName == "Alberta") shouldBe 0
        origCards.count(_.countryName == "Ontario") shouldBe 0

        mutWM.get.deckState.cards.count(_.countryName == "Alaska") shouldBe 1
        mutWM.get.deckState.cards.count(_.countryName == "Alberta") shouldBe 1
        mutWM.get.deckState.cards.count(_.countryName == "Ontario") shouldBe 1
      }

      "Armies should be awarded" in {
        mutWM.get.getActivePlayer.get.armies should be > origArmies
      }
      
      "Owned countries should receive an additional 2 units" in {
        mutWM.get.getCountry("Alaska").armies shouldBe wm.getCountry("Alaska").armies + 2
        mutWM.get.getCountry("Alberta").armies shouldBe wm.getCountry("Alberta").armies + 2
      }

      "Non owned countries should not receive an additional 2 units" in {
        mutWM.get.getCountry("Ontario").armies shouldBe wm.getCountry("Ontario").armies
      }
    }

    "If the cards are all 3 different" - {
      val cards = List(new Card("Alaska", Infantry), new Card("Alberta", Cavalry), new Card("Ontario", Artillery))
      val ownedCountries = List(("Alaska", true), ("Alberta", true), ("Ontario", false))
      val (mutWM, servInput, origArmies, origCards) = cardTestSetup(wm, cards, ownedCountries)

      servInput.receiveInput(TurnInCards(cards))

      "The cards should be removed from the player" in {
        mutWM.get.getActivePlayer.get.cards.size shouldBe 0
      }

      "The cards should be added back to the deck" in {
        origCards.count(_.countryName == "Alaska") shouldBe 0
        origCards.count(_.countryName == "Alberta") shouldBe 0
        origCards.count(_.countryName == "Ontario") shouldBe 0

        mutWM.get.deckState.cards.count(_.countryName == "Alaska") shouldBe 1
        mutWM.get.deckState.cards.count(_.countryName == "Alberta") shouldBe 1
        mutWM.get.deckState.cards.count(_.countryName == "Ontario") shouldBe 1
      }

      "Armies should be awarded" in {
        mutWM.get.getActivePlayer.get.armies should be > origArmies
      }

      "Owned countries should receive an additional 2 units" in {
        mutWM.get.getCountry("Alaska").armies shouldBe wm.getCountry("Alaska").armies + 2
        mutWM.get.getCountry("Alberta").armies shouldBe wm.getCountry("Alberta").armies + 2
      }
      
      "Non owned countries should not receive an additional 2 units" in {
        mutWM.get.getCountry("Ontario").armies shouldBe wm.getCountry("Ontario").armies
      }
    }

    "Not enough cards error" - {
      val cards = List(new Card("Alberta", Infantry), new Card("Ontario", Infantry))
      val ownedCountries = List(("Alberta", true), ("Ontario", false))
      val (mutWM, servInput, origArmies, origCards) = cardTestSetup(wm, cards, ownedCountries)
      servInput.receiveInput(TurnInCards(cards))

      "The cards should not be removed" in {
        mutWM.get.getActivePlayer.get.cards shouldBe cards
      }

      "The cards should not be added back to the deck" in {
        origCards.count(_.countryName == "Alberta") shouldBe 0
        origCards.count(_.countryName == "Ontario") shouldBe 0

        mutWM.get.deckState.cards.count(_.countryName == "Alberta") shouldBe 0
        mutWM.get.deckState.cards.count(_.countryName == "Ontario") shouldBe 0
      }

      "Armies should not be awarded" in {
        mutWM.get.getActivePlayer.get.armies shouldBe origArmies
      }

      "Owned countries should not receive an additional 2 units" in {
        mutWM.get.getCountry("Alberta").armies shouldBe wm.getCountry("Alberta").armies
      }

      "Non owned countries should not receive an additional 2 units" in {
        mutWM.get.getCountry("Ontario").armies shouldBe wm.getCountry("Ontario").armies
      }
    }

    "Too many cards error" - {
      val cards = List(new Card("Alaska", Infantry), new Card("Alberta", Infantry), new Card("Ontario", Infantry), new Card("NW Territory", Infantry))
      val ownedCountries = List(("Alaska", true), ("Alberta", true), ("Ontario", false), ("NW Territory", false))
      val (mutWM, servInput, origArmies, origCards) = cardTestSetup(wm, cards, ownedCountries)

      servInput.receiveInput(TurnInCards(cards))

      "The cards should not be removed" in {
        mutWM.get.getActivePlayer.get.cards shouldBe cards
      }

      "The cards should not be added back to the deck" in {
        origCards.count(_.countryName == "Alaska") shouldBe 0
        origCards.count(_.countryName == "Alberta") shouldBe 0
        origCards.count(_.countryName == "Ontario") shouldBe 0
        origCards.count(_.countryName == "NW Territory") shouldBe 0

        mutWM.get.deckState.cards.count(_.countryName == "Alaska") shouldBe 0
        mutWM.get.deckState.cards.count(_.countryName == "Alberta") shouldBe 0
        mutWM.get.deckState.cards.count(_.countryName == "Ontario") shouldBe 0
        mutWM.get.deckState.cards.count(_.countryName == "NW Territory") shouldBe 0
      }

      "Armies should not be awarded" in {
        mutWM.get.getActivePlayer.get.armies shouldBe origArmies
      }

      "Owned countries should not receive an additional 2 units" in {
        mutWM.get.getCountry("Alaska").armies shouldBe wm.getCountry("Alaska").armies
        mutWM.get.getCountry("Alberta").armies shouldBe wm.getCountry("Alberta").armies
      }

      "Non owned countries should not receive an additional 2 units" in {
        mutWM.get.getCountry("Ontario").armies shouldBe wm.getCountry("Ontario").armies
        mutWM.get.getCountry("NW Territory").armies shouldBe wm.getCountry("NW Territory").armies
      }
    }

    "Invalid armyType combination error" - {
      val cards = List(new Card("Alaska", Cavalry), new Card("Alberta", Infantry), new Card("Ontario", Infantry))
      val ownedCountries = List(("Alaska", true), ("Alberta", true), ("Ontario", false))
      val (mutWM, servInput, origArmies, origCards) = cardTestSetup(wm, cards, ownedCountries)
      servInput.receiveInput(TurnInCards(cards))

      "The cards should not be removed" in {
        mutWM.get.getActivePlayer.get.cards shouldBe cards
      }

      "The cards should not be added back to the deck" in {
        origCards.count(_.countryName == "Alaska") shouldBe 0
        origCards.count(_.countryName == "Alberta") shouldBe 0
        origCards.count(_.countryName == "Ontario") shouldBe 0

        mutWM.get.deckState.cards.count(_.countryName == "Alaska") shouldBe 0
        mutWM.get.deckState.cards.count(_.countryName == "Alberta") shouldBe 0
        mutWM.get.deckState.cards.count(_.countryName == "Ontario") shouldBe 0
      }

      "Armies should not be awarded" in {
        mutWM.get.getActivePlayer.get.armies shouldBe origArmies
      }

      "Owned countries should not receive an additional 2 units" in {
        mutWM.get.getCountry("Alaska").armies shouldBe wm.getCountry("Alaska").armies
        mutWM.get.getCountry("Alberta").armies shouldBe wm.getCountry("Alberta").armies
      }

      "Non owned countries should not receive an additional 2 units" in {
        mutWM.get.getCountry("Ontario").armies shouldBe wm.getCountry("Ontario").armies
      }
    }

    "TurnInCards Input sent when phase is not TurnPlacement" - {
      val cards = List(new Card("Alaska", Cavalry), new Card("Alberta", Infantry), new Card("Ontario", Infantry))
      val ownedCountries = List(("Alaska", true), ("Alberta", true), ("Ontario", false))
      val (mutWM, servInput, origArmies, origCards) = cardTestSetup(wm, cards, ownedCountries)
      mutWM.get.setPhase(Attacking(None,None))
      servInput.receiveInput(TurnInCards(cards))

      "The cards should not be removed" in {
        mutWM.get.getActivePlayer.get.cards shouldBe cards
      }

      "The cards should not be added back to the deck" in {
        origCards.count(_.countryName == "Alaska") shouldBe 0
        origCards.count(_.countryName == "Alberta") shouldBe 0
        origCards.count(_.countryName == "Ontario") shouldBe 0

        mutWM.get.deckState.cards.count(_.countryName == "Alaska") shouldBe 0
        mutWM.get.deckState.cards.count(_.countryName == "Alberta") shouldBe 0
        mutWM.get.deckState.cards.count(_.countryName == "Ontario") shouldBe 0
      }

      "Armies should not be awarded" in {
        mutWM.get.getActivePlayer.get.armies shouldBe origArmies
      }

      "Owned countries should not receive an additional 2 units" in {
        mutWM.get.getCountry("Alaska").armies shouldBe wm.getCountry("Alaska").armies
        mutWM.get.getCountry("Alberta").armies shouldBe wm.getCountry("Alberta").armies
      }

      "Non owned countries should not receive an additional 2 units" in {
        mutWM.get.getCountry("Ontario").armies shouldBe wm.getCountry("Ontario").armies
      }
    }
  }

  def cardTestSetup(origWM: WorldMap, cardsInHand:List[Card], updateOwners:List[(String, Boolean)]): (MutWM, ServiceInputHandler, Int, List[Card]) = {
    val mutWM = new MutWM(origWM)
    mutWM.get = mutWM.get.setPhase(TurnPlacement)

    cardsInHand.reverse foreach (c => mutWM.get = mutWM.get
      .updatePlayer(mutWM.get.getActivePlayer.get
          .setCountryTaken(true)
          .awardCard(c))
      .copy(deckState = mutWM.get.deckState.copy(cards = cardsInHand diff cardsInHand)))




    for(a<-updateOwners.indices) {
      if (updateOwners(a)._2) {
        mutWM.get = mutWM.get
          .updateSingleCountry(mutWM.get.getCountry(updateOwners(a)._1).copy(owner = mutWM.get.getActivePlayer))
      } else {
        mutWM.get = mutWM.get
          .updateSingleCountry(mutWM.get.getCountry(updateOwners(a)._1).copy(owner = mutWM.get.players.find(_ != mutWM.get.getActivePlayer.get)))
      }
    }


    val armies = mutWM.get.getActivePlayer.get.armies
    val servInput = new ServiceInputHandler(() => mutWM.get, new SideEffectManager((newWM: WorldMap) => {mutWM.get = newWM}))

    (mutWM, servInput, armies, mutWM.get.deckState.cards)
  }
}


class MutWM(wm:WorldMap){
  var get = wm
}