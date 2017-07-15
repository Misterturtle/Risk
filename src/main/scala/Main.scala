import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.paint.Color

/**
  * Created by Harambe on 6/16/2017.
  */
object Main extends JFXApp {

  val players = List[Player](HumanPlayer(1, 0, "red"), HumanPlayer(2,0, "blue"), ComputerPlayer(3, 0, "green"))
  private var mutableWorldMap = WorldMap(CountryFactory.getBlankCountries, players, 0, NotInGame, StateStamp(-1))
  private val worldMapUI = new WorldMapUI(mutableWorldMap)


  def mutateWorldMap(worldMap: WorldMap): Unit = {
    println("Mutated World Map")
    mutableWorldMap = worldMap
    worldMapUI.updateWorldMap(worldMap)
  }

  def getCurrentWorldMap: WorldMap = mutableWorldMap

  StateTransitions.begin(mutableWorldMap)


  stage = new PrimaryStage{
    scene = new Scene(worldMapUI, 800,600)
  }


}
