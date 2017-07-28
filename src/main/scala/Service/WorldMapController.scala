package Service

import GUI.{CustomColors, WorldMapUI}

/**
  * Created by Harambe on 7/18/2017.
  */
class WorldMapController() {

  val sideEffectManager = new SideEffectManager(mutateWorldMapUnsafe)

  val players = List[Player](HumanPlayer("Turtle", 1, 0, CustomColors.red), HumanPlayer("Boy Wonder", 2,0, CustomColors.blue), ComputerPlayer("Some Scrub", 3, 0, CustomColors.green))
  private var mutableWorldMap = WorldMap(CountryFactory.getCountries, players, 0, NotInGame)
  val wmUICont = new WorldMapUIController(getCurrentWorldMap _, sideEffectManager)
  val worldMapUI = new WorldMapUI(wmUICont)

  def mutateWorldMapUnsafe(wm:WorldMap) :Unit = {
    mutableWorldMap = wm
    worldMapUI.updateWorldMap(wmUICont)
  }

  def getCurrentWorldMap: WorldMap = mutableWorldMap





  //Entry Point
  def begin() = sideEffectManager.performEffect(Effects.begin(mutableWorldMap))

}
