import GUI.{CustomColors, WorldMapUI}
import Service._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FreeSpec, Matchers}
import org.mockito.Mockito._

/**
  * Created by Harambe on 7/19/2017.
  */
class AttackingPhaseTests extends FreeSpec with Matchers with MockitoSugar {

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

  mutableWorldMap = mutableWorldMap.setPhase(Attacking(None, None))
  mutableWorldMap = mutableWorldMap.updateSingleCountry(mutableWorldMap.getCountry(CountryFactory.ALASKA).copy(owner = mutableWorldMap.getPlayerByPlayerNumber(1), armies = 5))
  mutableWorldMap = mutableWorldMap.updateSingleCountry(mutableWorldMap.getCountry(CountryFactory.ALBERTA).copy(owner = mutableWorldMap.getPlayerByPlayerNumber(1), armies = 5))
  mutableWorldMap = mutableWorldMap.updateSingleCountry(mutableWorldMap.getCountry(CountryFactory.NW_TERRITORY).copy(owner = mutableWorldMap.getPlayerByPlayerNumber(2)))
  mutableWorldMap = mutableWorldMap.updateSingleCountry(mutableWorldMap.getCountry(CountryFactory.WESTERN_US).copy(owner = mutableWorldMap.getPlayerByPlayerNumber(2)))


  val beginAttackPhase = mutableWorldMap

  "Selecting a non owned country when no attacking source does nothing" in {
    val newWM = Effects.getCountryClickedEffect(beginAttackPhase, beginAttackPhase.getCountry(CountryFactory.NW_TERRITORY)).eval(StateStamp(-1))

    newWM shouldBe beginAttackPhase
  }

  "Selecting an owned country sets the Attacking Phase source to that country" in {
    val newWM = Effects.getCountryClickedEffect(beginAttackPhase, beginAttackPhase.getCountry(CountryFactory.ALASKA)).eval(StateStamp(-1))

    newWM.phase shouldBe Attacking(Some(beginAttackPhase.getCountry(CountryFactory.ALASKA)), None)
  }

  "Selecting the source country while it is selected should deselect it" in {
    val wmWithSourceSelected = beginAttackPhase.copy(phase = Attacking(Some(beginAttackPhase.getCountry(CountryFactory.ALASKA)), None))
    val newWM = Effects.getCountryClickedEffect(wmWithSourceSelected, wmWithSourceSelected.getCountry(CountryFactory.ALASKA)).eval(StateStamp(-1))

    newWM.phase shouldBe Attacking(None, None)
  }

  "Selecting a country with only 1 army should do nothing" in {
    val oneArmyWM = beginAttackPhase.updateSingleCountry(beginAttackPhase.getCountry(CountryFactory.ALASKA).copy(armies = 1))
    val newWM = Effects.getCountryClickedEffect(oneArmyWM, oneArmyWM.getCountry(CountryFactory.ALASKA)).eval(StateStamp(-1))

    newWM shouldBe oneArmyWM
  }

  "Selecting an owned country when a source is already selected does nothing" in {
    val wmWithSourceSelected = beginAttackPhase.copy(phase = Attacking(Some(beginAttackPhase.getCountry(CountryFactory.ALASKA)), None))
    val newWM = Effects.getCountryClickedEffect(wmWithSourceSelected, wmWithSourceSelected.getCountry("alberta")).eval(StateStamp(-1))

    newWM.phase shouldBe Attacking(Some(beginAttackPhase.getCountry(CountryFactory.ALASKA)), None)
  }

  "Selecting an adjacent non owned country when a source is already selected should begin a battle" in {
    val wmWithSourceSelected = beginAttackPhase.copy(phase = Attacking(Some(beginAttackPhase.getCountry(CountryFactory.ALASKA)), None))
    val newWM = Effects.getCountryClickedEffect(wmWithSourceSelected, wmWithSourceSelected.getCountry(CountryFactory.NW_TERRITORY)).eval(StateStamp(-1))

    newWM.phase shouldBe Battle(beginAttackPhase.getCountry(CountryFactory.ALASKA), beginAttackPhase.getCountry(CountryFactory.NW_TERRITORY))
  }

  "Selecting a non owned country that is NOT adjacent to the source should not do anything" in {
    val wmWithSourceSelected = beginAttackPhase.copy(phase = Attacking(Some(beginAttackPhase.getCountry(CountryFactory.ALASKA)), None))
    val newWM = Effects.getCountryClickedEffect(wmWithSourceSelected, wmWithSourceSelected.getCountry(CountryFactory.WESTERN_US)).eval(StateStamp(-1))

    newWM shouldBe wmWithSourceSelected
  }

  "If the EndAttackPhase input is received, the phase should transition to the Transfer phase" in {
    val wm = beginAttackPhase
    val newWM = Effects.endAttackPhase(wm).eval(StateStamp(-1))

    newWM.phase shouldBe Reinforcement(None,None)
  }
}
