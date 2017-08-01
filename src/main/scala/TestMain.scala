import javafx.beans.value.WritableValue

import scalafx.animation.{KeyValue, KeyFrame}
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.layout.{Region, VBox, AnchorPane, HBox}
import scalafx.scene.paint.Color
import scalafx.scene.shape.SVGPath
import scalafx.util.Duration

/**
  * Created by Harambe on 7/28/2017.
  */
object TestMain extends JFXApp {

  val root = new AnchorPane()
  root.style = "-fx-border-color:black"


  val vbox = new VBox()
  vbox.style = "-fx-border-color:red"
  vbox.prefHeight.bind(root.prefHeight)


  val hbox = new HBox()
  hbox.children.add(vbox)
  AnchorPane.setLeftAnchor(hbox, 100)
  AnchorPane.setRightAnchor(hbox, 100)
  AnchorPane.setTopAnchor(hbox, 100)
  AnchorPane.setBottomAnchor(hbox, 100)
  hbox.prefHeight.bind(root.prefHeight)
  hbox.prefWidth.bind(root.prefWidth)
  hbox.minHeight = Region.USE_PREF_SIZE

  hbox.style = "-fx-border-color:blue"

  root.children.add(hbox)





  stage = new PrimaryStage(){
    scene = new Scene(root, 800, 600)
  }
}

