package GUI.CustomControls

import javafx.event.EventHandler
import javafx.scene.input.MouseEvent

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.control.Button
import scalafx.scene.{Group, Scene}
import scalafx.scene.layout.{AnchorPane, HBox, StackPane}
import scalafx.scene.paint.Color
import scalafx.scene.shape.SVGPath

/**
  * Created by Rob on 7/29/2017.
  */
object SVGTests extends JFXApp {

  val root = new AnchorPane()

  stage = new PrimaryStage{
    scene = new Scene(root, 800,600)
  }

  def circle(width:Double, arcFactor: Double = .55): String =  {

    arc(1, width, arcFactor, false) + arc(2, width, arcFactor) + arc(3, width, arcFactor) + arc(4, width, arcFactor)
  }

  def arc(quadrant: Int, width:Double, arcFactor:Double = .55, continued: Boolean = true): String = {


    var quadrantOrigins = (0,0)
    var quadrantEndPoints = (0,0)


    var anchor1 = ""
    var anchor2 = ""



    quadrant match{
      case 1 =>
        quadrantOrigins = (0, -1)
        quadrantEndPoints = (1,0)
        anchor1 = s"C ${(width * arcFactor).toString},${(-1*width).toString} "
        anchor2 = s"${width.toString},${(-1*width * arcFactor).toString} "

      case 2 =>
        quadrantOrigins = (1, 0)
        quadrantEndPoints = (0, 1)
        anchor1 = s"C ${width.toString},${(width * arcFactor).toString} "
        anchor2 = s"${(width * arcFactor).toString},${width.toString} "
      case 3 =>
        quadrantOrigins = (0, 1)
        quadrantEndPoints = (-1, 0)
        anchor1 = s"C ${(-1*width * arcFactor).toString},${width.toString} "
        anchor2 = s"${(-1* width).toString},${(width * arcFactor).toString} "
      case 4 =>
        quadrantOrigins = (-1,0)
        quadrantEndPoints = (0, -1)
        anchor1 = s"C ${(-1*width).toString},${(-1*width * arcFactor).toString} "
        anchor2 = s"${(-1* width * arcFactor).toString},${(-1*width).toString} "
    }

    val move = s"M ${quadrantOrigins._1 * width},${quadrantOrigins._2 * width}"
    val endPoint = s"${quadrantEndPoints._1 * width},${quadrantEndPoints._2 * width}"

    if(continued)
      anchor1 + anchor2 + endPoint
    else
      move + anchor1 + anchor2 + endPoint
  }

  println(circle(1, .55))

  //M 0,-1 C .55,-1 1,-.55 1,0 C 1,.55 .55,1 0,1 C -.55,1 -1,.55 -1,0 C -1,-.55 -.55,-1 0,-1 z
  //M 0,-1 C .55,-1 1,-.55 1,0 C .55,1 1,.55 0,1 C -.55,1 -1,.55 -1,0 C -.55,-1 -1,-.55 0,-1 z

  val outerCircle = new SVGPath()
  outerCircle.setContent(circle(1)+ "L 0,-.8" + circle(-.8)+"z")
  outerCircle.setScaleX(40)
  outerCircle.setScaleY(40)
  outerCircle.setOpacity(.5)
  outerCircle.setFill(Color.Red)


  val innerCircle = new SVGPath()
  innerCircle.setContent(circle(-.8))
  innerCircle.setScaleX(40)
  innerCircle.setScaleY(40)
  innerCircle.setOpacity(.5)
  innerCircle.toFront()

  val arc = new SVGPath()
  arc.setContent(arc(1, -1, .55, false))
  arc.setScaleX(40)
  arc.setScaleY(40)

  val hollowCircle = new StackPane()
  hollowCircle.children.addAll(new Group(arc))
  hollowCircle.style = "-fx-border-color: red"

  val group = new HBox()
  group.children.addAll(hollowCircle)

  group.style = "-fx-border-color: red"

  AnchorPane.setAnchors(group, 100,100,100,100)

  root.children.add(group)
  root.style = "-fx-background-color: gray"

}
