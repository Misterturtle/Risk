import GUI.{CustomColors, WorldMapUI}
import Service._
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FreeSpec, Matchers}
import scalaz._

import common.Common._


class TurnPlacementTests extends FreeSpec with Matchers with MockitoSugar {

  val mockUI = mock[WorldMapUI]

  private val initialPlacementWorldMap = randomPlacementWorldMap()
  private val player1Country = initialPlacementWorldMap.countries.find(_.owner.map(_.playerNumber).contains(1)).get
  private val player2Country = initialPlacementWorldMap.countries.find(_.owner.map(_.playerNumber).contains(2)).get

  private val player2TurnWorldMap = Effects.countryClicked(player1Country).eval(StateStamp(-1, player2TurnWorldMap))


  "Taking the last turn in the InitPlacePhase should transition to Turn Placement Phase" - {

    val wmEffect = Effect { worldMap: WorldMap =>
        liftPlayer(Player.removeAllArmies(1))
        liftPlayer(Player.removeAllArmies(3))


      worldMap
    }

    val lastPlacement = initialPlacementWorldMap >>
      WorldMap.removeAllArmiesFromPlayer(1) >>
      WorldMap.removeAllArmiesFromPlayer(3) >>
      WorldMap.setArmiesForPlayer(2, 1)

    val turnPlaceWM = Effects.countryClicked(player2Country).eval(StateStamp(-1, player2TurnWorldMap))

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

  "During a human player's Turn Placement Phase" - {
    val wm = randomPlacementWorldMap()

    "Clicking on an unowned country should do nothing" in {
      val unownedCountry = wm.countries.find(_.owner.map(_.playerNumber).contains(3)).get
      val newWM = Effects.countryClicked(unownedCountry).eval(StateStamp(-1, wm))

      newWM shouldBe wm
    }

    "Clicking on an owned country should place an army" in {
      val ownedCountry = wm.countries.find(_.owner.map(_.playerNumber).contains(2)).get
      val newWM = Effects.countryClicked(ownedCountry).eval(StateStamp(-1, wm))

      newWM.getCountry(ownedCountry.name).armies shouldBe ownedCountry.armies + 1
    }

    "Placing the last army should transition to the attack phase" in {
      val ownedCountry = wm.countries.find(_.owner.map(_.playerNumber).contains(2)).get
      val attackPhaseWM = Effects.countryClicked(ownedCountry).eval(StateStamp(-1, wm))

      attackPhaseWM.phase shouldBe Attacking(None, None)
    }
  }

  def randomPlacementWorldMap(): WorldMap = {
    val players = List[Player](HumanPlayer("Turtle", 1, 0, CustomColors.red), HumanPlayer("Boy Wonder", 2, 0, CustomColors.blue), ComputerPlayer("Some Scrub", 3, 0, CustomColors.green))
    var _worldMap = new WorldMap(CountryFactory.getCountries, players, 1, InitialPlacement)

    for (a <- 1 to 14) {
      for (b <- 0 until 3) {
        def unownedCountry = _worldMap.countries.find(_.owner.isEmpty).get

        _worldMap = _worldMap.updateSingleCountry(unownedCountry.copy(armies = 1, owner = Some(players(b))))
      }
    }

    _worldMap
  }
}
