
import Service.WorldMapController

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.paint.Color

/**
  * Created by Harambe on 6/16/2017.
  */
object Main extends JFXApp {

  private val wmCont = new WorldMapController()

  wmCont.begin()



  stage = new PrimaryStage{
    scene = new Scene(wmCont.worldMapUI, 800,600)
  }



}
