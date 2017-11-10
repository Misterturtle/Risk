import javafx.embed.swing.JFXPanel

import GUI.{CustomColors, WorldMapUI}
import Service._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FreeSpec, Matchers}
import org.mockito.Mockito._

/**
  * Created by Harambe on 7/19/2017.
  */
class AttackingPhaseTests extends FreeSpec with Matchers with MockitoSugar {

  //Init Graphics
  new JFXPanel()

  val mockUI = mock[WorldMapUI]
  val players = List[Player](HumanPlayer("Turtle", 1, 1, CustomColors.red, Nil), HumanPlayer("Boy Wonder", 2, 1, CustomColors.blue, Nil), ComputerPlayer("Some Scrub", 3, 1, CustomColors.green, Nil))
  var mutableCountries = CountryFactory.getCountries
  var mutableWorldMap = new WorldMap(mutableCountries, players, 1, InitialPlacement)

  for (a <- 1 to 14) {
    for (b <- 0 until 3) {
      def unownedCountry = mutableWorldMap.countries.find(_.owner.isEmpty).get
      mutableWorldMap = mutableWorldMap.updateSingleCountry(unownedCountry.copy(armies = 1, owner = Some(players(b))))
    }
  }

  mutableWorldMap = mutableWorldMap.setPhase(Attacking(None, None))
  mutableWorldMap = mutableWorldMap.updateSingleCountry(mutableWorldMap.getCountry("Alaska").copy(owner = mutableWorldMap.getPlayerByPlayerNumber(1), armies = 5))
  mutableWorldMap = mutableWorldMap.updateSingleCountry(mutableWorldMap.getCountry("Alberta").copy(owner = mutableWorldMap.getPlayerByPlayerNumber(1), armies = 5))
  mutableWorldMap = mutableWorldMap.updateSingleCountry(mutableWorldMap.getCountry("NW Territory").copy(owner = mutableWorldMap.getPlayerByPlayerNumber(2)))
  mutableWorldMap = mutableWorldMap.updateSingleCountry(mutableWorldMap.getCountry("Western US").copy(owner = mutableWorldMap.getPlayerByPlayerNumber(2)))


  val beginAttackPhase = mutableWorldMap

  "Selecting a non owned country when no attacking source does nothing" in {
    val newWM = Effects.getCountryClickedEffect(beginAttackPhase, "NW Territory").eval(StateStamp(-1))

    newWM shouldBe beginAttackPhase
  }

  "Selecting an owned country sets the Attacking Phase source to that country" in {
    val newWM = Effects.getCountryClickedEffect(beginAttackPhase, "Alaska").eval(StateStamp(-1))

    newWM.phase shouldBe Attacking(Some(beginAttackPhase.getCountry("Alaska")), None)
  }

  "Selecting the source country while it is selected should deselect it" in {
    val wmWithSourceSelected = beginAttackPhase.copy(phase = Attacking(Some(beginAttackPhase.getCountry("Alaska")), None))
    val newWM = Effects.getCountryClickedEffect(wmWithSourceSelected, "Alaska").eval(StateStamp(-1))

    newWM.phase shouldBe Attacking(None, None)
  }

  "Selecting a country with only 1 army should do nothing" in {
    val oneArmyWM = beginAttackPhase.updateSingleCountry(beginAttackPhase.getCountry("Alaska").copy(armies = 1))
    val newWM = Effects.getCountryClickedEffect(oneArmyWM, "Alaska").eval(StateStamp(-1))

    newWM shouldBe oneArmyWM
  }

  "Selecting an owned country when a source is already selected does nothing" in {
    val wmWithSourceSelected = beginAttackPhase.copy(phase = Attacking(Some(beginAttackPhase.getCountry("Alaska")), None))
    val newWM = Effects.getCountryClickedEffect(wmWithSourceSelected, "Alberta").eval(StateStamp(-1))

    newWM.phase shouldBe Attacking(Some(beginAttackPhase.getCountry("Alaska")), None)
  }

  "Selecting an adjacent non owned country when a source is already selected should begin a battle" in {
    val wmWithSourceSelected = beginAttackPhase.copy(phase = Attacking(Some(beginAttackPhase.getCountry("Alaska")), None))
    val newWM = Effects.getCountryClickedEffect(wmWithSourceSelected, "NW Territory").eval(StateStamp(-1))

    newWM.phase shouldBe Battle(beginAttackPhase.getCountry("Alaska"), beginAttackPhase.getCountry("NW Territory"))
  }

  "Selecting a non owned country that is NOT adjacent to the source should not do anything" in {
    val wmWithSourceSelected = beginAttackPhase.copy(phase = Attacking(Some(beginAttackPhase.getCountry("Alaska")), None))
    val newWM = Effects.getCountryClickedEffect(wmWithSourceSelected, "Western US").eval(StateStamp(-1))

    newWM shouldBe wmWithSourceSelected
  }

  "If the EndAttackPhase input is received, the phase should transition to the Transfer phase" in {
    val wm = beginAttackPhase
    val newWM = Effects.endAttackPhase(wm).eval(StateStamp(-1))

    newWM.phase shouldBe Reinforcement(None,None)
  }


}
