import GUI.{CustomColors, WorldMapUI}
import Service._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FreeSpec, Matchers}
import common.Common._


class TurnPlacementTests extends FreeSpec with Matchers with MockitoSugar {

  val mockUI = mock[WorldMapUI]

  val createThreePlayers: WorldMap => WorldMap = (worldMap: WorldMap) => {
    val players = List[Player](HumanPlayer("Turtle", 1, 0, CustomColors.red), HumanPlayer("Boy Wonder", 2, 0, CustomColors.blue), ComputerPlayer("Some Scrub", 3, 0, CustomColors.green))
    worldMap.copy(players = players)
  }

  val placeRandomInitialArmies: WorldMap => WorldMap = (worldMap: WorldMap) => {
    var _worldMap = worldMap
    val countriesPerPlayer = worldMap.countries.size / worldMap.players.size

    for (a <- 1 to countriesPerPlayer) {
      for (b <- worldMap.players.indices) {
        def unownedCountry = _worldMap.countries.find(_.owner.isEmpty).get

        _worldMap = _worldMap.updateSingleCountry(unownedCountry.copy(armies = 1, owner = Some(worldMap.players(b))))
      }
    }

    _worldMap
  }

  private val initialPlacementWorldMap = WorldMap.INITIAL >>
    createThreePlayers >>
    placeRandomInitialArmies

  "Taking the last turn in the InitPlacePhase should transition to Turn Placement Phase" - {

    val localWorldMap = initialPlacementWorldMap >>
      WorldMap.setPhase(InitialPlacement) >>
      Player.setArmiesForPlayer(2, 1) >>
      WorldMap.setActivePlayer(2)

    val player2Country = initialPlacementWorldMap.countries.find(_.owner.map(_.playerNumber).contains(2)).get

    val turnPlaceWM = Actions.countryClicked(player2Country).run(localWorldMap)

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

    val activePlayerNumber = 2
    val nonActivePlayerNumber = 3

    val localWorldMap = initialPlacementWorldMap >>
      WorldMap.setPhase(TurnPlacement) >>
      Player.setArmiesForPlayer(activePlayerNumber, 1) >>
      WorldMap.setActivePlayer(activePlayerNumber)

    "Clicking on an unowned country should do nothing" in {
      val unownedCountry = localWorldMap.countries.find(_.owner.map(_.playerNumber).contains(nonActivePlayerNumber)).get
      val newWM = Actions.countryClicked(unownedCountry).run(localWorldMap)

      newWM shouldBe localWorldMap
    }

    "Clicking on an owned country should place an army" in {
      val ownedCountry = localWorldMap.countries.find(_.owner.map(_.playerNumber).contains(activePlayerNumber)).get
      val newWM = Actions.countryClicked(ownedCountry).run(localWorldMap)

      newWM.getCountry(ownedCountry.name).armies shouldBe ownedCountry.armies + 1
    }

    "Placing the last army should transition to the attack phase" in {
      val ownedCountry = localWorldMap.countries.find(_.owner.map(_.playerNumber).contains(activePlayerNumber)).get
      val attackPhaseWM = Actions.countryClicked(ownedCountry).run(localWorldMap)

      attackPhaseWM.phase shouldBe Attacking(None, None)
    }
  }
}
