import org.scalatest.{FreeSpec, Matchers}
import org.scalatest.mockito.MockitoSugar
import org.mockito.Mockito._

/**
  * Created by Harambe on 7/6/2017.
  */
class CompInitPlaceAITests extends FreeSpec with Matchers with MockitoSugar {

  "If all countries are not owned, the comp player should place an army on a non-owned country" in{
    val mockPlayer = mock[ComputerPlayer]
    val mockCountry = mock[Country]
    when(mockCountry.owner).thenReturn(Some(mockPlayer))
    val mockCountry2 = mock[Country]
    when(mockCountry2.owner).thenReturn(None)
    val mockCountries = Map[String,Country]("mock1" -> mockCountry, "mock2" -> mockCountry2)
    val state = CompInitPlaceAIState(mockPlayer, ()=>{}, mockCountries)

    state.update()

    verify(mockCountry2, times(1)).setOwner(mockPlayer)
    verify(mockCountry2, times(1)).addArmies(1)
    verify(mockPlayer, times(1)).removeAvailableArmies(1)
  }

  "After placing an army on a non-owned country, the comp player should call the endTurnMethod passed in" in {
    var turnEnded = false
    val mockPlayer = mock[ComputerPlayer]
    val mockCountry = mock[Country]
    when(mockCountry.owner).thenReturn(Some(mockPlayer))
    val mockCountry2 = mock[Country]
    when(mockCountry2.owner).thenReturn(None)
    val mockCountries = Map[String,Country]("mock1" -> mockCountry, "mock2" -> mockCountry2)
    val state = CompInitPlaceAIState(mockPlayer, ()=>{turnEnded = true}, mockCountries)

    state.update()

    turnEnded shouldBe true
  }


  "If all countries are owned, the comp player should place an army on a country the player owns" in {
    val mockPlayer = mock[ComputerPlayer]
    val mockPlayer2 = mock[ComputerPlayer]
    val mockCountry = mock[Country]
    when(mockCountry.owner).thenReturn(Some(mockPlayer))
    val mockCountry2 = mock[Country]
    when(mockCountry2.owner).thenReturn(Some(mockPlayer2))
    val mockCountries = Map[String,Country]("mock1" -> mockCountry, "mock2" -> mockCountry2)
    val state = CompInitPlaceAIState(mockPlayer, ()=>{}, mockCountries)

    state.update()

    verify(mockPlayer, times(1)).removeAvailableArmies(1)
    verify(mockCountry, times(1)).addArmies(1)
  }


  "After placing an army on an owned country, the comp player should call the endTurnMethod passed in" in {
    var turnEnded = false
    val mockPlayer = mock[ComputerPlayer]
    val mockPlayer2 = mock[ComputerPlayer]
    val mockCountry = mock[Country]
    when(mockCountry.owner).thenReturn(Some(mockPlayer))
    val mockCountry2 = mock[Country]
    when(mockCountry2.owner).thenReturn(Some(mockPlayer2))
    val mockCountries = Map[String,Country]("mock1" -> mockCountry, "mock2" -> mockCountry2)
    val state = CompInitPlaceAIState(mockPlayer, ()=>{turnEnded = true}, mockCountries)

    state.update()

    turnEnded shouldBe true
  }






}
