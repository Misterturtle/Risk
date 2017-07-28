import GUI.{CustomColors, WorldMapUI}
import Service._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FreeSpec, Matchers}
import org.mockito.Mockito._


/*
  * Created by Harambe on 7/19/2017.
  */
class ReinforcementPhaseTests extends FreeSpec with Matchers with MockitoSugar {

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

  mutableWorldMap = mutableWorldMap.setPhase(Reinforcement(None, None))
  mutableWorldMap = mutableWorldMap.updateSingleCountry(mutableWorldMap.getCountry("alaska").copy(owner = mutableWorldMap.getPlayerByPlayerNumber(1), armies = 25))
  mutableWorldMap = mutableWorldMap.updateSingleCountry(mutableWorldMap.getCountry("alberta").copy(owner = mutableWorldMap.getPlayerByPlayerNumber(1), armies = 10))


  val beginReinforcementPhase = mutableWorldMap


  "If a non-owned country is selected, nothing should happen" in {
    val wm = Effects.getCountryClickedEffect(beginReinforcementPhase, beginReinforcementPhase.getCountry("nwTerritory")).eval(StateStamp(-1))


    wm shouldBe beginReinforcementPhase
  }

  "If a owned country is selected, the source should be set to that country" in {
    val wm = Effects.getCountryClickedEffect(beginReinforcementPhase, beginReinforcementPhase.getCountry("alaska")).eval(StateStamp(-1))

    wm.phase.asInstanceOf[Reinforcement].source shouldBe Some(wm.getCountry("alaska"))
  }

  "If the source is selected a second time, it should deselect the source" in {
    val wm = Effects.getCountryClickedEffect(beginReinforcementPhase, beginReinforcementPhase.getCountry("alaska")).eval(StateStamp(-1))
    val wm2 = Effects.getCountryClickedEffect(wm, wm.getCountry("alaska")).eval(StateStamp(-1))

    wm2.phase.asInstanceOf[Reinforcement].source shouldBe None
  }

  "If a non-owned country is selected as the target, nothing should happen" in {
    val wm = Effects.getCountryClickedEffect(beginReinforcementPhase, beginReinforcementPhase.getCountry("alaska")).eval(StateStamp(-1))
    val wm2 = Effects.getCountryClickedEffect(wm, wm.getCountry("nwTerritory")).eval(StateStamp(-1))

    wm2.phase.asInstanceOf[Reinforcement].target shouldBe None
  }

  "If a non-adjacent owned country is selected as the target, nothing should happen" in {
    val wm = beginReinforcementPhase.updateSingleCountry(beginReinforcementPhase.getCountry("ontario").setOwner(beginReinforcementPhase.getPlayerByPlayerNumber(1).get))
    val wm2 = Effects.getCountryClickedEffect(wm, wm.getCountry("alaska")).eval(StateStamp(-1))
    val wm3 = Effects.getCountryClickedEffect(wm2, wm2.getCountry("ontario")).eval(StateStamp(-1))

    wm3.phase.asInstanceOf[Reinforcement].target shouldBe None
  }

  "If a adjacent owned country is selected as the target, the target should be set" in {
    val wm = Effects.getCountryClickedEffect(beginReinforcementPhase, beginReinforcementPhase.getCountry("alaska")).eval(StateStamp(-1))
    val wm2 = Effects.getCountryClickedEffect(wm, wm.getCountry("alberta")).eval(StateStamp(-1))

    wm2.phase.asInstanceOf[Reinforcement].target shouldBe Some(wm2.getCountry("alberta"))
  }

  "If a Service.ConfirmTransfer input is received" - {
    val source = beginReinforcementPhase.getCountry("alaska").copy(armies = 25)
    val target = beginReinforcementPhase.getCountry("alberta").copy(armies = 1)
    val wm = beginReinforcementPhase.setPhase(Reinforcement(Some(source), Some(target))).updateSomeCountries(List(source,target))
    val confirmation = ConfirmTransfer(10)
    val wm2 = Effects.executeTransfer(wm, confirmation).eval(StateStamp(-1))

    "Transfer those armies" in {
      wm2.getCountry("alaska").armies shouldBe 15
      wm2.getCountry("alberta").armies shouldBe 11
    }

    "Begin the next player turn" in {
      wm2.activePlayerNumber shouldBe 2
    }

    "The phase should be set to TurnPlacement" in {
      wm2.phase shouldBe TurnPlacement
    }
  }






}
