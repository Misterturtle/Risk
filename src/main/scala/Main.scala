
import Service.{SideEffectManager, WorldMapController, WorldMapUIController}
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene

object Main extends JFXApp {

  private val wmCont = new WorldMapController()
  private val wmUICont = new WorldMapUIController(wmCont.getCurrentWorldMap _)
  private val sideEffectManager = new SideEffectManager(wmCont, wmUICont)
  SideEffectManager.setNewSingleton(sideEffectManager)

  stage = new PrimaryStage{
    scene = new Scene(wmUICont.worldMapUI, 1200,800)
  }

  val list: List[Integer] = List(1,2)

  wmUICont.worldMapUI.postInit()
  wmCont.begin()
}
