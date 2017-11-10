package Service

import scalafx.scene.image.Image
import scalafx.scene.shape.SVGPath

/**
  * Created by Harambe on 8/26/2017.
  */
trait ArmyType {
  val image:Image
}
case object Infantry extends ArmyType {
  val image = new Image("/green_army.png")
}
case object Cavalry extends ArmyType{
  val image = new Image("/cavalry.jpg")
}
case object Artillery extends ArmyType{
  val image = new Image("/artillery.jpg")
}