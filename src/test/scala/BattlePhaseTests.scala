import GUI.{CustomColors, WorldMapUI}
import Service.CountryFactory._
import Service._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FreeSpec, Matchers}
import org.mockito.Mockito._

import scala.util.Random

/**
  * Created by Harambe on 7/19/2017.
  */
class BattlePhaseTests extends FreeSpec with Matchers with MockitoSugar {

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


  mutableWorldMap = mutableWorldMap.updateSingleCountry(mutableWorldMap.getCountry(ALASKA).copy(owner = mutableWorldMap.getPlayerByPlayerNumber(1)))
  mutableWorldMap = mutableWorldMap.updateSingleCountry(mutableWorldMap.getCountry(ALBERTA).copy(owner = mutableWorldMap.getPlayerByPlayerNumber(1)))
  mutableWorldMap = mutableWorldMap.updateSingleCountry(mutableWorldMap.getCountry(NW_TERRITORY).copy(owner = mutableWorldMap.getPlayerByPlayerNumber(2)))
  mutableWorldMap = mutableWorldMap.updateSingleCountry(mutableWorldMap.getCountry(WESTERN_US).copy(owner = mutableWorldMap.getPlayerByPlayerNumber(2)))
  mutableWorldMap = mutableWorldMap.setPhase(Battle(mutableWorldMap.getCountry(ALASKA), mutableWorldMap.getCountry(NW_TERRITORY)))


  val beginBattlePhase = mutableWorldMap


  "When battling with 3 attackers and 2 defenders, it should return the correct BattleResults" in {
    val mockRandom = mock[RandomFactory]
    when(mockRandom.roll()).thenReturn(6,3,5,4,3)
    val battleResult = BattleResult(rGen = mockRandom)

    battleResult.attack(3,2) shouldBe battleResult.copy(offRolls = List(6,3,5), defRolls = List(4,3))
  }

  "If the attacker has 2 higher die and 1 lower compared to defender, the defender should lose 2" in {
    val mockRandom = mock[RandomFactory]
    when(mockRandom.roll()).thenReturn(6,3,5,4,3)
    val battleResult = BattleResult(rGen = mockRandom)

    battleResult.attack(3,2).offDefArmiesLost() shouldBe (0,2)
  }

  "If the attacker has 1 higher die and 2 lower compared to defender, the defender should lose 1 and attacker should lose 1" in {
    val mockRandom = mock[RandomFactory]
    when(mockRandom.roll()).thenReturn(6,1,1,4,4)
    val battleResult = BattleResult(rGen = mockRandom)

    battleResult.attack(3,2).offDefArmiesLost() shouldBe (1,1)
  }

  "If the attacker has 3 equal die and compared to defender, the attacker should lose 2" in {
    val mockRandom = mock[RandomFactory]
    when(mockRandom.roll()).thenReturn(1,1,1,1,1)
    val battleResult = BattleResult(rGen = mockRandom)

    battleResult.attack(3,2).offDefArmiesLost() shouldBe (2,0)
  }

  "If the defender only has 1 die, the highest offensive roll is compared" in {
    val mockRandom = mock[RandomFactory]
    when(mockRandom.roll()).thenReturn(1,1,4,2)
    val battleResult = BattleResult(rGen = mockRandom)

    battleResult.attack(3,1).offDefArmiesLost() shouldBe (0,1)
  }

  "When the ConfirmAttack input is received," - {

    val wm = beginBattlePhase
      .updateSingleCountry(beginBattlePhase.getCountry(NW_TERRITORY).copy(armies = 5))
      .updateSingleCountry(beginBattlePhase.getCountry(ALASKA).copy(armies = 4))

    val preBattle = wm.setPhase(Battle(wm.getCountry(ALASKA), wm.getCountry(NW_TERRITORY)))
    val mockRandom = mock[RandomFactory]
    when(mockRandom.roll()).thenReturn(1, 1, 5, 4, 2)
    val battle = preBattle.phase.asInstanceOf[Battle]
    val postBattle = Actions.executeBattle(ConfirmBattle(battle.source, battle.target, 3), mockRandom).run(preBattle)

    "it should record the battle" in {
      postBattle.phase.asInstanceOf[Battle].previousBattle shouldBe Some(BattleResult(List(1, 1, 5), List(4, 2), mockRandom))
    }

    "it should remove the armies lost from the attacking country" in {
      postBattle.getCountry(ALASKA).armies shouldBe 3
    }

    "it should remove the armies lost from the defending country" in {
      postBattle.getCountry(NW_TERRITORY).armies shouldBe 4
    }
  }


  "If the attack reduces the defending armies to zero, " - {
    val wm = beginBattlePhase.updateSingleCountry(beginBattlePhase.getCountry(NW_TERRITORY).copy(armies = 1))
    val preBattle = wm.setPhase(Battle(wm.getCountry(ALASKA), wm.getCountry(NW_TERRITORY)))
    val mockRandom = mock[RandomFactory]
    when(mockRandom.roll()).thenReturn(1, 1, 5, 1)
    val battle = preBattle.phase.asInstanceOf[Battle]
    val postBattle = Actions.executeBattle(ConfirmBattle(battle.source, battle.target, 3), mockRandom).run(preBattle)


    "The phase should active the transferring flag" in {
      postBattle.phase.asInstanceOf[Battle].isTransferring shouldBe true
    }

    "The target country should have it's owner set to the attacking player" in {
      postBattle.getCountry(NW_TERRITORY).owner.map(_.playerNumber).contains(postBattle.activePlayerNumber)
    }
  }

  "If the attack reduces the attacking armies to one, " - {
    val wm = beginBattlePhase.updateSingleCountry(beginBattlePhase.getCountry(NW_TERRITORY).copy(armies = 1))
    val preBattle = wm.setPhase(Battle(wm.getCountry(ALASKA), wm.getCountry(NW_TERRITORY)))
    val mockRandom = mock[RandomFactory]
    when(mockRandom.roll()).thenReturn(1, 1, 1, 6)
    val battle = preBattle.phase.asInstanceOf[Battle]
    val postBattle = Actions.executeBattle(ConfirmBattle(battle.source, battle.target, 3), mockRandom).run(preBattle)

    "The attacking country should retreat" in {
      postBattle.phase shouldBe Attacking(None, None)
    }

  }

  "If the ConfirmTransfer input is received while the isTransferring flag is set to true" - {
    val source = beginBattlePhase.getCountry(ALASKA).copy(armies = 25)
    val target = beginBattlePhase.getCountry(NW_TERRITORY).copy(armies = 0, owner = beginBattlePhase.getActivePlayer)
    val wm = beginBattlePhase.setPhase(Battle(source, target, None, true))
    val postTransfer = Actions.executeBattleTransfer(ConfirmTransfer(10)).run(wm)


   "The amount of armies should be transferred" in {
     postTransfer.getCountry(ALASKA).armies shouldBe 15
     postTransfer.getCountry(NW_TERRITORY).armies shouldBe 10
   }

    "The phase should be set back to Service.Attacking" in {
      postTransfer.phase shouldBe Attacking(None, None)
    }
  }

  "If the Retreat input is received, the phase should go back to Attacking" in {
    val wm = beginBattlePhase
    val postRetreat = Actions.retreatFromBattle().run(wm)

    postRetreat.phase shouldBe Attacking(None, None)
  }
}
