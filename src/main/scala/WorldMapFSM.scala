/**
  * Created by Harambe on 6/18/2017.
  */
class WorldMapFSM(val players:List[Player], worldUI:WorldUI = new WorldUI) extends FSM {

  val countries = worldUI.countries

  object initialPlacement{
    private var activePlayer = 1

    val activePlayerHasArmiesToPlace = players(activePlayer-1).armiesToPlace > 0


    var initialPlacement = State()
  }





}
