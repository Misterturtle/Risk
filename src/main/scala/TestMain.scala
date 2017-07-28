import javafx.beans.value.WritableValue

import scalafx.animation.{KeyValue, KeyFrame}
import scalafx.scene.layout.HBox
import scalafx.util.Duration

/**
  * Created by Harambe on 7/28/2017.
  */
object TestMain extends App {

  val box = new HBox()
  val kf = KeyFrame(Duration.Zero, values = Set(KeyValue(box.scaleX, 2)))


/*
using a companion object with a generic apply method that then does the asInstanceOf
 */




}

