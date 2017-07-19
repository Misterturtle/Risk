import Main._

import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene

/**
  * Created by Harambe on 7/18/2017.
  */
class WorldMapController() {

  val sideEffectManager = new SideEffectManager(mutateWorldMapUnsafe)

  val players = List[Player](HumanPlayer("Turtle", 1, 0, "red"), HumanPlayer("Boy Wonder", 2,0, "blue"), ComputerPlayer("Some Scrub", 3, 0, "green"))
  private var mutableWorldMap = WorldMap(CountryFactory.getBlankCountries, players, 0, NotInGame)
  val wmInputHandler = new WorldMapInputHandler(getCurrentWorldMap _, sideEffectManager)
  val worldMapUI = new WorldMapUI(mutableWorldMap, wmInputHandler)

  def mutateWorldMapUnsafe(wm:WorldMap) :Unit = {
    mutableWorldMap = wm
    worldMapUI.updateWorldMap(wm, wmInputHandler)
  }

  def getCurrentWorldMap: WorldMap = mutableWorldMap





  //Entry Point
  def begin() = sideEffectManager.performEffect(Effects.begin(mutableWorldMap))

}
