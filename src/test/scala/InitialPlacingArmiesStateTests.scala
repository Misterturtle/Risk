import javafx.embed.swing.JFXPanel

import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{Matchers, FreeSpec}

import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.layout.Pane

/**
  * Created by Harambe on 6/18/2017.
  */
class InitialPlacingArmiesStateTests extends FreeSpec with Matchers with MockitoSugar {
  val test = new JFXPanel()


  "If a country is not owned during InitPlaceState and the active player is human, when clicked it should" - {

    val mockPlayer = mock[HumanPlayer]
    when(mockPlayer.availableArmies).thenReturn(35)
    val mockPlayer2 = mock[HumanPlayer]
    val mockPlayer3 = mock[ComputerPlayer]
    val mockCountry = mock[Country]
    when(mockCountry.owner).thenReturn(None)
    interceptClickActions(mockCountry)

    val mockCountries = Map[String, Country]("country1" -> mockCountry)
    val initialPlacementState = InitPlaceState(List(mockPlayer, mockPlayer2, mockPlayer3), mockCountries)

    initialPlacementState.setup()
    initialPlacementState.update()
    mockCountry.doClickAction()

    "Remove an army from the player" in {
      verify(mockPlayer, times(1)).removeAvailableArmies(1)
    }

    "Set the countries owner to the player" in {
      verify(mockCountry, times(1)).setOwner(mockPlayer)
    }

    "Add one to the countries armies" in {
      verify(mockCountry, times(1)).addArmies(1)
    }

    "End the turn after an army is placed" in {
      initialPlacementState.activePlayer shouldBe mockPlayer2
    }

    "Not place a 2nd army if clicked multiple times" in {
      mockCountry.doClickAction()
      verify(mockCountry, times(1)).setOwner(mockPlayer)
      verify(mockCountry, times(1)).addArmies(1)
    }
  }


  "A player should not be able to place a 2nd army on a country until all countries are owned" in {

    val mockPlayer = mock[HumanPlayer]
    when(mockPlayer.availableArmies).thenReturn(35)
    val mockPlayer2 = mock[HumanPlayer]
    val mockPlayer3 = mock[ComputerPlayer]
    val mockCountry = mock[Country]
    when(mockCountry.owner).thenReturn(Some(mockPlayer))
    val mockCountry2 = mock[Country]
    when(mockCountry2.owner).thenReturn(Some(mockPlayer))
    val mockCountry3 = mock[Country]
    when(mockCountry3.owner).thenReturn(None)
    interceptClickActions(mockCountry)
    interceptClickActions(mockCountry3)
    val mockCountries = Map[String, Country]("country1" -> mockCountry, "country2" -> mockCountry2, "country3" -> mockCountry3)
    val initialPlacementState = InitPlaceState(List(mockPlayer, mockPlayer2, mockPlayer3), mockCountries)
    initialPlacementState.setup()
    initialPlacementState.update()

    mockCountry.doClickAction()
    mockCountry3.doClickAction()

    verify(mockCountry, never()).addArmies(any())
    verify(mockCountry3, times(1)).addArmies(any())
  }

  "If all countries are owned, when attempting to click on a country a player should" - {

    val mockPlayer = mock[HumanPlayer]
    val mockPlayer2 = mock[HumanPlayer]
    val mockCountry = mock[Country]
    when(mockCountry.owner).thenReturn(Some(mockPlayer))
    interceptClickActions(mockCountry)
    val mockCountry2 = mock[Country]
    when(mockCountry2.owner).thenReturn(Some(mockPlayer))
    val mockCountries = Map[String, Country]("mock1" -> mockCountry, "mock2" -> mockCountry2)
    val initialPlacementState = InitPlaceState(List(mockPlayer, mockPlayer2), mockCountries)
    initialPlacementState.update()
    mockCountry.doClickAction()

    "Be able to place an army on any country he owns" in {
      verify(mockCountry, times(1)).addArmies(1)
      verify(mockPlayer, times(1)).removeAvailableArmies(1)
    }

    "Still end their turn after placing an army" in {
      initialPlacementState.activePlayer shouldBe mockPlayer2
    }
  }


  "If the active player has no available armies left, the state should activate the return transition" in {
    val mockPlayer = mock[HumanPlayer]
    val mockCountries = Map[String, Country]()
    val initialPlacementState = InitPlaceState(List(mockPlayer), mockCountries)
    initialPlacementState.setup()

    initialPlacementState.update()

    initialPlacementState.returnState shouldBe true
  }


  "On the first update cycle of the InitPlaceState, players should receive certain amounts of armies" - {

    "When there are only 3 players, each player should start with 35 available armies" in {
      val humanPlayer = mock[HumanPlayer]
      val compPlayer = mock[ComputerPlayer]
      val compPlayer2 = mock[ComputerPlayer]
      val countries = Map[String, Country]()
      val state = InitPlaceState(List(humanPlayer, compPlayer, compPlayer2), countries)

      state.update()

      verify(humanPlayer, times(1)).addAvailableArmies(35)
      verify(compPlayer, times(1)).addAvailableArmies(35)
      verify(compPlayer2, times(1)).addAvailableArmies(35)
    }

    "When there are only 4 players, each player should start with 30 available armies" in {
      val humanPlayer = mock[HumanPlayer]
      val compPlayer = mock[ComputerPlayer]
      val compPlayer2 = mock[ComputerPlayer]
      val compPlayer3 = mock[ComputerPlayer]
      val countries = Map[String, Country]()
      val state = InitPlaceState(List(humanPlayer, compPlayer, compPlayer2, compPlayer3), countries)

      state.update()

      verify(humanPlayer, times(1)).addAvailableArmies(30)
      verify(compPlayer, times(1)).addAvailableArmies(30)
      verify(compPlayer2, times(1)).addAvailableArmies(30)
      verify(compPlayer3, times(1)).addAvailableArmies(30)
    }

    "When there are only 5 players, each player should start with 25 available armies" in {
      val humanPlayer = mock[HumanPlayer]
      val compPlayer = mock[ComputerPlayer]
      val compPlayer2 = mock[ComputerPlayer]
      val compPlayer3 = mock[ComputerPlayer]
      val compPlayer4 = mock[ComputerPlayer]
      val countries = Map[String, Country]()
      val state = InitPlaceState(List(humanPlayer, compPlayer, compPlayer2, compPlayer3, compPlayer4), countries)

      state.update()

      verify(humanPlayer, times(1)).addAvailableArmies(25)
      verify(compPlayer, times(1)).addAvailableArmies(25)
      verify(compPlayer2, times(1)).addAvailableArmies(25)
      verify(compPlayer3, times(1)).addAvailableArmies(25)
      verify(compPlayer4, times(1)).addAvailableArmies(25)
    }
  }

  "The _forward state of InitPlaceState should be CompInitPlaceAI if the active player is a computer player and has equal or more armies than another player" in {
        val compPlayer = mock[ComputerPlayer]
        when(compPlayer.availableArmies).thenReturn(35)
        val compPlayer2 = mock[ComputerPlayer]
        when(compPlayer2.availableArmies).thenReturn(35)
        val compPlayer3 = mock[ComputerPlayer]
        when(compPlayer3.availableArmies).thenReturn(35)
        val countries = Map[String, Country]()
        val state = InitPlaceState(List(compPlayer, compPlayer2, compPlayer3), countries)

        state.setup()
        state.update()


        state.forwardState.get() shouldBe CompInitPlaceAIState(compPlayer, state.endPlayersTurn, Map[String,Country]())
  }

  "After forwarding the state to a CompInitPlaceAI, the active player should change" in {
    val compPlayer = mock[ComputerPlayer]
    when(compPlayer.availableArmies).thenReturn(35)
    val compPlayer2 = mock[ComputerPlayer]
    when(compPlayer2.availableArmies).thenReturn(35)
    val compPlayer3 = mock[ComputerPlayer]
    when(compPlayer3.availableArmies).thenReturn(35)
    val country1 = mock[Country]
    when(country1.owner).thenReturn(None)
    val countries = Map[String,Country]("Country1" -> country1)
    val state = InitPlaceState(List(compPlayer, compPlayer2, compPlayer3), countries)

    state.setup()
    state.update()
    state.activePlayer shouldBe compPlayer
    state.update()
    state.activePlayer shouldBe compPlayer2
    state.update()

    state.update()
    state.activePlayer shouldBe compPlayer3
    state.activePlayer shouldBe compPlayer
  }



  def interceptClickActions(mockCountry: Country): Unit = {
    var onClickArgs = () => {}

    doAnswer(new Answer[Unit] {
      override def answer(invocation: InvocationOnMock): Unit = {
        onClickArgs = invocation.getArgument(0).asInstanceOf[() => Unit]
      }
    }).when(mockCountry).setClickAction(any())

    doAnswer(new Answer[Unit] {
      override def answer(invocation: InvocationOnMock): Unit = {
        onClickArgs()
      }
    }).when(mockCountry).doClickAction()
  }

}
