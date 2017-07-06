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

    verify(mockCountry2, times(1)).addArmies(1)
  }





}
