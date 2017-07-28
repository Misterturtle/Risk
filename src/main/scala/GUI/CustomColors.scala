package GUI

import javafx.scene.paint.{CycleMethod, Color, LinearGradient, Stop}


/**
  * Created by Harambe on 7/26/2017.
  */
object CustomColors {

  val red = new LinearGradient(0,0,0,1,true,CycleMethod.NO_CYCLE, new Stop(0, Color.rgb(255,139,158)), new Stop(.25, Color.valueOf("#CE001C")), new Stop(.75, Color.valueOf("#CE001C")), new Stop(1, Color.rgb(101,0,28)))
  val green = new LinearGradient(0,0,0,1,true,CycleMethod.NO_CYCLE, new Stop(0, Color.valueOf("#88e486")), new Stop(.25, Color.valueOf("#037A1C")), new Stop(.75, Color.valueOf("#037A1C")), new Stop(1, Color.valueOf("#03310f")))
  val blue = new LinearGradient(0,0,0,1,true,CycleMethod.NO_CYCLE, new Stop(0, Color.valueOf("#66b2ff")), new Stop(.25, Color.valueOf("#245DBA")), new Stop(.75, Color.valueOf("#245DBA")), new Stop(1, Color.valueOf("#1c1c39")))
  val brown = new LinearGradient(0,0,0,1,true,CycleMethod.NO_CYCLE, new Stop(0, Color.valueOf("#eccbb5")), new Stop(.25, Color.valueOf("#77410C")), new Stop(.75, Color.valueOf("#77410C")), new Stop(1, Color.valueOf("#31180d")))
  val yellow = new LinearGradient(0,0,0,1,true,CycleMethod.NO_CYCLE, new Stop(0, Color.valueOf("#f4f4b0")), new Stop(.25, Color.valueOf("#DDDD00")), new Stop(.75, Color.valueOf("#DDDD00")), new Stop(1, Color.valueOf("#807d22")))
  val black = new LinearGradient(0,0,0,1,true,CycleMethod.NO_CYCLE, new Stop(0, Color.valueOf("#5c5c5c")), new Stop(.25, Color.valueOf("#141414")), new Stop(.75, Color.valueOf("#141414")), new Stop(1, Color.valueOf("#000000")))
  val pink = new LinearGradient(0,0,0,1,true,CycleMethod.NO_CYCLE, new Stop(0, Color.valueOf("#f8baff")), new Stop(.25, Color.valueOf("#FF73FF")), new Stop(.75, Color.valueOf("#FF73FF")), new Stop(1, Color.valueOf("#603060")))
  val gray = new LinearGradient(0,0,0,1,true,CycleMethod.NO_CYCLE, new Stop(0, Color.valueOf("#d3d3d3")), new Stop(.25, Color.valueOf("#7b7b7b")), new Stop(.75, Color.valueOf("#7b7b7b")), new Stop(1, Color.valueOf("#3b3b3b")))

}
