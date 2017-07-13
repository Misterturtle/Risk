import javafx.animation.AnimationTimer
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

import _root_.WorldMap.WorldMapState

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.layout.AnchorPane
import scalaz.Scalaz._
import scalaz.State

/**
  * Created by Harambe on 6/16/2017.
  */
object Main extends JFXApp {

  private var mutableWorldMap = WorldMap(Nil, Nil, None, NotInGame, StateStamp(-1))

  private val worldMapUI =


  def mutateWorldMap(worldMap: WorldMap): Unit = {
    println("Mutated World Map")
    mutableWorldMap = worldMap
  }

  def getCurrentWorldMap: WorldMap = mutableWorldMap





}
