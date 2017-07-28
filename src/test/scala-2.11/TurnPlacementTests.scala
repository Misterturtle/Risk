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

  val mockUI = mock[WorldMapUI]
  val players = List[Player](HumanPlayer("Turtle", 1, 1, CustomColors.red), HumanPlayer("Boy Wonder", 2, 1, CustomColors.blue), ComputerPlayer("Some Scrub", 3, 1, CustomColors.green))
  var mutableCountries = CountryFactory.getCountries
  var mutableWorldMap = new WorldMap(mutableCountries, players, 1, InitialPlacement)

  for (a <- 1 to 14) {
    for (b <- 0 until 3) {
      def unownedCountry = mutableWorldMap.countries.find(_.owner.isEmpty).get
      mutableWorldMap = mutableWorldMap.updateSingleCountry(unownedCountry.copy(armies = 1, owner = Some(players(b))))
    }
  }

  val player1Country = mutableWorldMap.countries.find(_.owner.map(_.playerNumber).contains(1)).get
  val player2Country = mutableWorldMap.countries.find(_.owner.map(_.playerNumber).contains(2)).get
  mutableWorldMap = Effects.getCountryClickedEffect(mutableWorldMap, player1Country).eval(StateStamp(-1))


  "Taking the last turn in the InitPlacePhase should transition to Turn Placement Service.Phase" - {

    val turnPlaceWM = Effects.getCountryClickedEffect(mutableWorldMap, player2Country).eval(StateStamp(-1))

    "The phase should be set to TurnPlacement" in {
      turnPlaceWM.phase shouldBe TurnPlacement
    }

    "Set player 1 as the active player" in {
      turnPlaceWM.activePlayerNumber shouldBe 1
    }

    "Allocate 4 armies to player 1" in {
      turnPlaceWM.getActivePlayer.get.armies shouldBe 4
    }
  }

  "During a human player's Turn Placement Service.Phase" - {

    val wm = Effects.getCountryClickedEffect(mutableWorldMap, player2Country).eval(StateStamp(-1))

    "Clicking on an unowned country should do nothing" in {
      val unownedCountry = wm.countries.find(_.owner.map(_.playerNumber).contains(2)).get
      val newWM = Effects.getCountryClickedEffect(wm, unownedCountry).eval(StateStamp(-1))

      newWM shouldBe wm
    }

    "Clicking on an owned country should place an army" in {
      val ownedCountry = wm.countries.find(_.owner.map(_.playerNumber).contains(1)).get
      val newWM = Effects.getCountryClickedEffect(wm, ownedCountry).eval(StateStamp(-1))

      newWM.getActivePlayer.get.armies shouldBe 3
      newWM.getCountry(ownedCountry.name).armies shouldBe ownedCountry.armies + 1
    }

    "Placing the last army should transition to the attack phase" in {
      val lastPlacementWM = wm.updatePlayer(wm.getActivePlayer.get.removeArmies(3))
      val ownedCountry = wm.countries.find(_.owner.map(_.playerNumber).contains(1)).get
      val attackPhaseWM = Effects.getCountryClickedEffect(lastPlacementWM, ownedCountry).eval(StateStamp(-1))

      attackPhaseWM.phase shouldBe Attacking(None)
    }
  }
}
