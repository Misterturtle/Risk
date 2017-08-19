package GUI

import javafx.beans.property.{DoubleProperty, SimpleDoubleProperty}
import javafx.beans.value.{ObservableValue, ChangeListener}
import javafx.geometry.Bounds
import javafx.scene.paint.Paint

import scalafx.animation._
import scalafx.scene.shape.{Line, Shape, SVGPath}
import scalafx.util.Duration
import scalaz._
import Scalaz._


import Service.{WorldMapUIController, Battle, Attacking, Phase}

import scalafx.geometry.Pos
import scalafx.scene.Group
import scalafx.scene.layout.{Priority, VBox, StackPane, HBox}
import scalafx.scene.text.Text

/**
  * Created by Harambe on 7/27/2017.
  */

class SVGArmyDisplay() extends StackPane {


  val displayCircle = new SVGPath()
  displayCircle.setContent("M40.251,0.5c-15.768,0-24.957,12.625-24.957,24.749c0,21.001,17.332,25.251,24.957,25.251c12,0,25.043-8.27,25.043-25.251C65.294,11.493,54.188,0.5,40.251,0.5z")
  displayCircle.setFill(CustomColors.gray)
  displayCircle.scaleX = .8
  displayCircle.scaleY = .8

  val armyText = new Text("0")
  armyText.setScaleX(2)
  armyText.setScaleY(2)

  val displayCircleStackPane = new StackPane()
  displayCircleStackPane.children.addAll(displayCircle, armyText)


  this.setMouseTransparent(true)
  alignment = Pos.Center
  children.addAll(displayCircleStackPane, armyText)

  def update(armies:Int, color:Paint): Unit = {
    displayCircle.setFill(color)
    armyText.setText(armies.toString)
  }
}