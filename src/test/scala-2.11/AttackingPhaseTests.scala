import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FreeSpec, Matchers}
import org.mockito.Mockito._

/**
  * Created by Harambe on 7/19/2017.
  */
class AttackingPhaseTests extends FreeSpec with Matchers with MockitoSugar {

  val mockUI = mock[WorldMapUI]
  val players = List[Player](HumanPlayer("Turtle", 1, 1, "red"), HumanPlayer("Boy Wonder", 2, 1, "blue"), ComputerPlayer("Some Scrub", 3, 1, "green"))
  var mutableCountries = CountryFactory.getBlankCountries
  var mutableWorldMap = new WorldMap(mutableCountries, players, 1, InitialPlacement)

  for (a <- 1 to 14) {
    for (b <- 0 until 3) {
      def unownedCountry = mutableWorldMap.countries.find(_.owner.isEmpty).get
      mutableWorldMap = mutableWorldMap.updateSingleCountry(unownedCountry.copy(armies = 1, owner = Some(players(b))))
    }
  }
  mutableWorldMap = mutableWorldMap.copy(phase = Attacking(None, None))

  "During a human player's Attacking phase" - {

    val beginAttackPhase = mutableWorldMap

    "Selecting a non owned country when no attacking source does nothing" in {
      val nonOwnedCountry = beginAttackPhase.countries.find(_.owner.map(_.playerNumber).contains(2)).get
      val newWM = Effects.getCountryClickedEffect(beginAttackPhase, nonOwnedCountry).eval(StateStamp(-1))

      newWM shouldBe beginAttackPhase
    }

    "Selecting an owned country sets the Attacking Phase source to that country" in {
      val ownedCountry = beginAttackPhase.countries.find(_.owner.map(_.playerNumber).contains(1)).get
      val newWM = Effects.getCountryClickedEffect(beginAttackPhase, ownedCountry).eval(StateStamp(-1))

      newWM.phase shouldBe Attacking(Some(ownedCountry), None)
    }






  }



}
