import javafx.scene.image.Image
import javafx.scene.layout.{BackgroundImage, Background}


import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.geometry.Pos
import scalafx.scene.layout.{VBox, AnchorPane}
import scalafx.scene.{Group, Scene}
import scalafx.scene.shape.{Circle, SVGPath}

/**
  * Created by Harambe on 7/28/2017.
  */
object TestMain extends JFXApp {

  val root = new AnchorPane()




  val targetCircleShape = new SVGPath()
  targetCircleShape.setContent("M1.079,0.555c-0.005-0.112-0.048-0.222-0.128-0.31l0.053-0.054L0.969,0.156L0.915,0.209c-0.088-0.08-0.198-0.123-0.31-0.128V0.005h-0.05v0.076C0.451,0.086,0.348,0.124,0.264,0.193L0.209,0.138L0.173,0.173l0.053,0.053C0.135,0.318,0.087,0.435,0.081,0.555H0.005v0.05h0.076c0.005,0.112,0.048,0.222,0.128,0.31L0.156,0.969l0.035,0.035l0.054-0.053c0.088,0.08,0.198,0.123,0.31,0.128v0.076h0.05V1.079c0.112-0.005,0.222-0.048,0.31-0.128l0.054,0.053l0.035-0.035L0.951,0.915c0.08-0.088,0.123-0.198,0.128-0.31h0.076v-0.05H1.079z M0.863,0.863c-0.156,0.156-0.41,0.156-0.566,0c-0.156-0.156-0.156-0.41,0-0.566c0.156-0.156,0.41-0.156,0.566,0C1.019,0.453,1.019,0.707,0.863,0.863z")
  targetCircleShape.setScaleX(70)
  targetCircleShape.setScaleY(70)



  val targetCircle = new VBox()
  targetCircle.children.add(targetCircleShape)
  targetCircle.style = "-fx-border-color: blue"
  targetCircle.scaleX = 5
  targetCircle.scaleY = 5



  val targetCircleContainer = new VBox()
  targetCircleContainer.children.add(targetCircle)

  targetCircleContainer.alignment = Pos.Center
  targetCircleContainer.translateX.bind(targetCircleContainer.width.delegate.divide(-2))
  targetCircleContainer.translateY.bind(targetCircleContainer.height.delegate.divide(-2))
  AnchorPane.setLeftAnchor(targetCircleContainer, 50)
  AnchorPane.setTopAnchor(targetCircleContainer, 50)
  targetCircleContainer.style = "-fx-border-color: red"

  val country = new AnchorPane()
  country.children.add(targetCircleContainer)
  AnchorPane.setTopAnchor(country, 200)
  AnchorPane.setLeftAnchor(country, 600)
  country.style = "-fx-border-color: black"


  root.children.add(country)



  stage = new PrimaryStage(){
    scene = new Scene(root, 1281.6, 851.52 )
  }
}

