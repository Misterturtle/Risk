package Service

import GUI.{CustomColors, WorldMapUI}

/**
  * Created by Harambe on 7/18/2017.
  */
class WorldMapController() {

  val players = List[Player](HumanPlayer("Turtle", 1, 0, CustomColors.red), HumanPlayer("Boy Wonder", 2,0, CustomColors.blue), ComputerPlayer("Some Scrub", 3, 0, CustomColors.green))
  private var mutableWorldMap = WorldMap(CountryFactory.getCountries, players, 0, NotInGame)

  def mutateWorldMapUnsafe(wm:WorldMap) :Unit = {
    mutableWorldMap = wm
  }

  def getCurrentWorldMap: WorldMap = mutableWorldMap

  //Entry Point
  def begin() = SideEffectManager.receive(Effects.begin(mutableWorldMap))
}
