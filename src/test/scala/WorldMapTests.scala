import javafx.embed.swing.JFXPanel

import org.scalatest.{FreeSpec, Matchers}
import org.scalatest.mockito.MockitoSugar
import org.mockito.Mockito._

/**
  * Created by Harambe on 7/8/2017.
  */
class WorldMapTests extends FreeSpec with Matchers with MockitoSugar {

  //Init graphics
  new JFXPanel()

  "The world map should init players with their player numbers" in {
    val player1 = mock[HumanPlayer]
    val player2 = mock[HumanPlayer]
    val player3 = mock[ComputerPlayer]
    val player4 = mock[ComputerPlayer]
    val worldMap = new WorldMap(new CountryFactory, List(player1, player2, player3, player4))

    worldMap.init()

    verify(player1, times(1)).init(1)
    verify(player2, times(1)).init(2)
    verify(player3, times(1)).init(3)
    verify(player4, times(1)).init(4)
  }


}
