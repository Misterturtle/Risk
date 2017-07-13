import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene

/**
  * Created by Harambe on 6/16/2017.
  */
object Main extends JFXApp {


  private var mutableWorldMap = WorldMap(Nil, Nil, None, NotInGame, StateStamp(-1))

  private val worldMapUI = new WorldMapUI(mutableWorldMap)


  def mutateWorldMap(worldMap: WorldMap): Unit = {
    println("Mutated World Map")
    mutableWorldMap = worldMap
    worldMapUI.update()
  }

  def getCurrentWorldMap: WorldMap = mutableWorldMap

  StateTransitions.begin(mutableWorldMap)


  stage = new PrimaryStage{
    scene = new Scene(worldMapUI, 800,600)
  }


}
