
import Service.WorldMapController

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene


/**
  * Created by Harambe on 6/16/2017.
  */
object Main extends JFXApp {

  private val wmCont = new WorldMapController()

  stage = new PrimaryStage{
    scene = new Scene(wmCont.worldMapUI, 1200,800)
  }

  val list: List[Integer] = List(1,2)


  wmCont.worldMapUI.postInit()
  wmCont.begin()



}
