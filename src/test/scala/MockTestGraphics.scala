import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.layout.Pane

/**
  * Created by Harambe on 6/23/2017.
  */
class MockTestGraphics(root: Pane) extends JFXApp {


  stage = new PrimaryStage(){
    scene = new Scene(root, 1000, 800)
  }
}
