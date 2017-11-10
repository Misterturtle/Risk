import javafx.embed.swing.JFXPanel

import GUI.CustomColors
import Service._
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FreeSpec, Matchers}

/**
  * Created by Harambe on 9/5/2017.
  */
class ComputerDelayTests extends FreeSpec with Matchers with MockitoSugar{

  //Init Graphics
  new JFXPanel()

  val players = List[Player](HumanPlayer("Turtle", 1, 0, CustomColors.red, Nil), HumanPlayer("Boy Wonder", 2, 0, CustomColors.blue, Nil), ComputerPlayer("Some Scrub", 3, 0, CustomColors.green, Nil, new CompAsyncDelay))
  var mutableCountries = CountryFactory.getCountries
  var mutableWorldMap = new WorldMap(mutableCountries, players, 3, TurnPlacement)

  for (a <- 1 to 14) {
    for (b <- 0 until 3) {
      def unownedCountry = mutableWorldMap.countries.find(_.owner.isEmpty).get
      mutableWorldMap = mutableWorldMap.updateSingleCountry(unownedCountry.copy(armies = 1, owner = Some(players(b))))
    }
  }

  val wm = mutableWorldMap

  "The computer should delay to 'think' about his army placement when starting his move" in {

    val result = TurnPlacePhase.beginCompTurn(wm)

    result shouldBe wm
  }

  "The computer should execute the placementDelay after the beginTurnDelay" in {
    var isCalled = false
    val computerPlayer = wm.getActivePlayer.get.asInstanceOf[ComputerPlayer]
    val mockServInput = mock[ServiceInputHandler]
    when(mockServInput.receiveComputerDelay(PlacementDelay)).thenAnswer(new Answer[Unit] {
      override def answer(invocation: InvocationOnMock): Unit = isCalled = true
    })
    val compAsync = new CompAsyncDelay(mockServInput){
      override val beginTurnDelay = 50
    }
    val wm2 = wm.updatePlayer(computerPlayer.copy(compAsyncDelay = compAsync, armies = 1))

    TurnPlacePhase.beginCompTurn(wm2)

    Thread.sleep(75)
    isCalled shouldBe true
  }

  "The computer should repeat PlacementDelay for each army he owns" in {
    var isCalled = 0
    var mutWM = wm
    val SEM = new SideEffectManager((wm:WorldMap) => mutWM = wm)
    val computerPlayer = wm.getActivePlayer.get.asInstanceOf[ComputerPlayer]
    val servInput = new ServiceInputHandler(()=> mutWM, ()=> SEM){
      def normalCompInput = new ServiceInputHandler(()=> mutWM, ()=> SEM).receiveComputerDelay(_)
        override def receiveComputerDelay(input:CompInput) = {
          isCalled += 1
          normalCompInput(input)
      }
    }
    val compAsync = new CompAsyncDelay(servInput){
      override val placementDelay = 10
    }
    val wm2 = wm.updatePlayer(computerPlayer.copy(compAsyncDelay = compAsync, armies = 4))

    TurnPlacePhase.beginCompTurn(wm2)

    Thread.sleep(75)
    isCalled shouldBe 4
  }
}
