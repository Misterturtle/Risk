import javafx.animation.AnimationTimer
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.layout.AnchorPane

/**
  * Created by Harambe on 6/16/2017.
  */
object Main extends JFXApp {

  val worldUI = new WorldMap(new CountryFactory, List(new HumanPlayer(), new HumanPlayer()))

  val gameLoop = new AnimationTimer() {
    override def handle(now: Long): Unit = {
      worldUI.baseState.update()
    }
  }

  stage = new PrimaryStage() {
    scene = new Scene(worldUI, 1000, 800)
  }


  worldUI.init()
  worldUI.enableDebug()
  gameLoop.start()
}
