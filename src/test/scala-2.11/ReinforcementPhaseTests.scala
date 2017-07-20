import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FreeSpec, Matchers}
import org.mockito.Mockito._


/*
  * Created by Harambe on 7/19/2017.
  */
class ReinforcementPhaseTests extends FreeSpec with Matchers with MockitoSugar {

  val mockUI = mock[WorldMapUI]
  val players = List[Player](HumanPlayer("Turtle", 1, 1, "red"), HumanPlayer("Boy Wonder", 2, 1, "blue"), ComputerPlayer("Some Scrub", 3, 1, "green"))
  var mutableCountries = CountryFactory.getCountries
  var mutableWorldMap = new WorldMap(mutableCountries, players, 1, InitialPlacement)

  for (a <- 1 to 14) {
    for (b <- 0 until 3) {
      def unownedCountry = mutableWorldMap.countries.find(_.owner.isEmpty).get
      mutableWorldMap = mutableWorldMap.updateSingleCountry(unownedCountry.copy(armies = 1, owner = Some(players(b))))
    }
  }

  mutableWorldMap = mutableWorldMap.setPhase(Reinforcement(None, None))
  mutableWorldMap = mutableWorldMap.updateSingleCountry(mutableWorldMap.getCountry("alaska").copy(owner = mutableWorldMap.getPlayerByPlayerNumber(1), armies = 25))
  mutableWorldMap = mutableWorldMap.updateSingleCountry(mutableWorldMap.getCountry("alberta").copy(owner = mutableWorldMap.getPlayerByPlayerNumber(1), armies = 10))


  val beginReinforcementPhase = mutableWorldMap


  "If a non-owned country is selected, nothing should happen" in {
    val wm = Effects.getCountryClickedEffect(beginReinforcementPhase, beginReinforcementPhase.getCountry("nwTerritory"))

    wm shouldBe beginReinforcementPhase
  }




}
