import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FreeSpec, Matchers}
import org.mockito.Mockito._

import scala.util.Random

/**
  * Created by Harambe on 7/19/2017.
  */
class BattlePhaseTests extends FreeSpec with Matchers with MockitoSugar {

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


  mutableWorldMap = mutableWorldMap.updateSingleCountry(mutableWorldMap.getCountry("alaska").copy(owner = mutableWorldMap.getPlayerByPlayerNumber(1)))
  mutableWorldMap = mutableWorldMap.updateSingleCountry(mutableWorldMap.getCountry("alberta").copy(owner = mutableWorldMap.getPlayerByPlayerNumber(1)))
  mutableWorldMap = mutableWorldMap.updateSingleCountry(mutableWorldMap.getCountry("nwTerritory").copy(owner = mutableWorldMap.getPlayerByPlayerNumber(2)))
  mutableWorldMap = mutableWorldMap.updateSingleCountry(mutableWorldMap.getCountry("westernUS").copy(owner = mutableWorldMap.getPlayerByPlayerNumber(2)))
  mutableWorldMap = mutableWorldMap.setPhase(Battle(mutableWorldMap.getCountry("alaska"), mutableWorldMap.getCountry("nwTerritory")))


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




}
