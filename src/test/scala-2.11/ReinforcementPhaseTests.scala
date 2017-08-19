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

  mutableWorldMap = mutableWorldMap
    .setPhase(Reinforcement(None, None))
    .updateSingleCountry(mutableWorldMap.getCountry("Alaska").copy(owner = mutableWorldMap.getPlayerByPlayerNumber(1), armies = 25))
    .updateSingleCountry(mutableWorldMap.getCountry("Alberta").copy(owner = mutableWorldMap.getPlayerByPlayerNumber(1), armies = 10))
    .updateSingleCountry(mutableWorldMap.getCountry("Greenland").copy(owner = mutableWorldMap.getPlayerByPlayerNumber(1), armies = 1))


  val beginRP = mutableWorldMap


  "If a non-owned country is selected, nothing should happen" in {
    val wm = Effects.getCountryClickedEffect(beginRP, "NW Territory").eval(StateStamp(-1))


    wm shouldBe beginRP
  }

  "A source country with one army should not be a valid source country" in {
    ReinforcementPhase.isValidSource(beginRP, beginRP.getCountry("Greenland")) shouldBe false
  }

  "If a valid source country is selected, the source should be set to that country" in {
    val wm = Effects.getCountryClickedEffect(beginRP, "Alaska").eval(StateStamp(-1))

    wm.phase.asInstanceOf[Reinforcement].source shouldBe Some(wm.getCountry("Alaska"))
  }

  "A country should be a valid source to select even if the player's data has changed" in {
    val changedPlayer = beginRP.getActivePlayer.get.addArmies(5)
    val wm = beginRP.updateSingleCountry(beginRP.getCountry("Alaska").copy(owner = Some(changedPlayer)))

    ReinforcementPhase.isValidSource(wm, wm.getCountry("Alaska")) shouldBe true
  }

  "A country should be a valid target to select even if the player's data has changed" in {
    val changedPlayer = beginRP.getActivePlayer.get.addArmies(5)
    val wm = beginRP
      .setPhase(Reinforcement(Some(beginRP.getCountry("Alaska")), None))
      .updateSingleCountry(beginRP.getCountry("Alberta").copy(owner = Some(changedPlayer)))



    ReinforcementPhase.isValidTarget(wm, wm.getCountry("Alberta")) shouldBe true
  }

  "If the source is selected a second time, it should deselect the source" in {
    val wm = Effects.getCountryClickedEffect(beginRP, "Alaska").eval(StateStamp(-1))
    val wm2 = Effects.getCountryClickedEffect(wm, "Alaska").eval(StateStamp(-1))

    wm2.phase.asInstanceOf[Reinforcement].source shouldBe None
  }

  "If the source is selected a second time after the target has been selected, nothing should happen" in {
    val wm = beginRP.setPhase(Reinforcement(Some(beginRP.getCountry("Alaska")), Some(beginRP.getCountry("Alberta"))))
    val wm2 = Effects.getCountryClickedEffect(wm, "Alaska").eval(StateStamp(-1))

    wm2.phase shouldBe Reinforcement(Some(beginRP.getCountry("Alaska")), Some(beginRP.getCountry("Alberta")))
  }

  "If a non-owned country is selected as the target, nothing should happen" in {
    val wm = Effects.getCountryClickedEffect(beginRP, "Alaska").eval(StateStamp(-1))
    val wm2 = Effects.getCountryClickedEffect(wm, "NW Territory").eval(StateStamp(-1))

    wm2.phase.asInstanceOf[Reinforcement].target shouldBe None
  }

  "If a non-adjacent owned country is selected as the target, nothing should happen" in {
    val wm = beginRP.updateSingleCountry(beginRP.getCountry("Ontario").setOwner(beginRP.getPlayerByPlayerNumber(1).get))
    val wm2 = Effects.getCountryClickedEffect(wm, "Alaska").eval(StateStamp(-1))
    val wm3 = Effects.getCountryClickedEffect(wm2, "Ontario").eval(StateStamp(-1))

    wm3.phase.asInstanceOf[Reinforcement].target shouldBe None
  }

  "If a adjacent owned country is selected as the target, the target should be set" in {
    val wm = Effects.getCountryClickedEffect(beginRP, "Alaska").eval(StateStamp(-1))
    val wm2 = Effects.getCountryClickedEffect(wm, "Alberta").eval(StateStamp(-1))

    wm2.phase.asInstanceOf[Reinforcement].target shouldBe Some(wm2.getCountry("Alberta"))
  }

  "If a Service.ConfirmTransfer input is received" - {
    val source = beginRP.getCountry("Alaska").copy(armies = 25)
    val target = beginRP.getCountry("Alberta").copy(armies = 1)
    val wm = beginRP.setPhase(Reinforcement(Some(source), Some(target))).updateSomeCountries(List(source,target))
    val confirmation = ConfirmTransfer(10)
    val wm2 = Effects.executeTransfer(wm, confirmation).eval(StateStamp(-1))

    "Transfer those armies" in {
      wm2.getCountry("Alaska").armies shouldBe 15
      wm2.getCountry("Alberta").armies shouldBe 11
    }

    "Begin the next player turn" in {
      wm2.activePlayerNumber shouldBe 2
    }

    "The phase should be set to TurnPlacement" in {
      wm2.phase shouldBe TurnPlacement
    }
  }

  "If a CancelTransfer input is received" - {
    val source = beginRP.getCountry("Alaska").copy(armies = 25)
    val target = beginRP.getCountry("Alberta").copy(armies = 1)
    val wm = beginRP.setPhase(Reinforcement(Some(source), Some(target))).updateSomeCountries(List(source,target))
    val wm2 = Effects.cancelReinforcementTransfer(wm).eval(StateStamp(-1))

    "Transfer no armies" in {
      wm2.getCountry("Alaska").armies shouldBe 25
      wm2.getCountry("Alberta").armies shouldBe 1
    }

    "The phase should be reset to default Reinforcment" in {
      wm2.phase shouldBe Reinforcement(None,None)
    }
  }


  "If a EndTurn input is received" - {
    val wm = beginRP.setPhase(Reinforcement(None, None))
    val wm2 = Effects.endTurn(wm).eval(StateStamp(-1))

    "Begin the next player turn" in {
      wm2.activePlayerNumber shouldBe 2
    }

    "The phase should be set to TurnPlacement" in {
      wm2.phase shouldBe TurnPlacement
    }


  }






}
