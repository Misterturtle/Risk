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

import scalafx.geometry.{ Pos}
import scalafx.scene.Group
import scalafx.scene.layout.{Priority, VBox, StackPane, HBox}
import scalafx.scene.text.Text

/**
  * Created by Harambe on 7/27/2017.
  */

class SVGArmyDisplay(xScale: SimpleDoubleProperty, yScale: SimpleDoubleProperty) extends StackPane {


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



  val sourceCircle = new SVGPath()
  sourceCircle.setContent("M0.505,0.005c-0.276,0-0.5,0.224-0.5,0.5c0,0.276,0.224,0.5,0.5,0.5s0.5-0.224,0.5-0.5C1.005,0.229,0.781,0.005,0.505,0.005z M0.505,0.905c-0.221,0-0.4-0.179-0.4-0.4c0-0.221,0.179-0.4,0.4-0.4s0.4,0.179,0.4,0.4C0.905,0.726,0.726,0.905,0.505,0.905z")
  sourceCircle.setScaleX(70)
  sourceCircle.setScaleY(70)
  val sourceCircleGroup = new Group(sourceCircle)

  val targetCircle = new SVGPath()
  targetCircle.setContent("M1.079,0.555c-0.005-0.112-0.048-0.222-0.128-0.31l0.053-0.054L0.969,0.156L0.915,0.209c-0.088-0.08-0.198-0.123-0.31-0.128V0.005h-0.05v0.076C0.451,0.086,0.348,0.124,0.264,0.193L0.209,0.138L0.173,0.173l0.053,0.053C0.135,0.318,0.087,0.435,0.081,0.555H0.005v0.05h0.076c0.005,0.112,0.048,0.222,0.128,0.31L0.156,0.969l0.035,0.035l0.054-0.053c0.088,0.08,0.198,0.123,0.31,0.128v0.076h0.05V1.079c0.112-0.005,0.222-0.048,0.31-0.128l0.054,0.053l0.035-0.035L0.951,0.915c0.08-0.088,0.123-0.198,0.128-0.31h0.076v-0.05H1.079z M0.863,0.863c-0.156,0.156-0.41,0.156-0.566,0c-0.156-0.156-0.156-0.41,0-0.566c0.156-0.156,0.41-0.156,0.566,0C1.019,0.453,1.019,0.707,0.863,0.863z")
  targetCircle.setScaleX(70)
  targetCircle.setScaleY(70)
  val targetCircleGroup = new Group(targetCircle)


  this.scaleX.bind(xScale)
  this.scaleY.bind(yScale)
  this.setMouseTransparent(true)
  alignment = Pos.Center
  children.addAll(displayCircleStackPane, armyText)

  def update(countryName: String, wmUICont:WorldMapUIController): Unit = {
    val country = wmUICont.getCountryByName(countryName)
    displayCircle.setFill(country.owner.map(_.color).getOrElse(CustomColors.gray))
    armyText.setText(country.armies.toString)
    checkAnimations(countryName, wmUICont.getPhase)
  }

  def checkAnimations(countryName: String, phase:Phase): Unit = {

    phase match{

      case Battle(s,t,_,__) if t.name == countryName =>
        targetCircle.setFill(s.owner.map(_.color).get)
        activateTargetCountryAnim()

      case Battle(s,_,_,_) if s.name == countryName =>
        //Don't deactivate animation yet that began in Attacking Phase

      case Attacking(s, _) if s.map(_.name).getOrElse("None") == countryName =>
        sourceCircle.setFill(s.map(_.owner.map(_.color).get).get)
        activateSourceCountryAnim()

      case _ if isAnimating =>
        deactivateAnimations()

      case _ =>
    }
  }

  def isAnimating:Boolean = sourceCountryAnimActive || targetCountryAnimActive
  private var sourceCountryAnimActive = false
  private var targetCountryAnimActive = false

  val pulseCircleAnim = new ScaleTransition(new Duration(1000))



  def activateSourceCountryAnim(): Unit = {
    if(!sourceCountryAnimActive){

      println("Source Animation starting")
      sourceCountryAnimActive = true

      this.children.add(sourceCircleGroup)

      pulseCircleAnim.setNode(sourceCircleGroup)
      pulseCircleAnim.byX = .3
      pulseCircleAnim.byY = .3
      pulseCircleAnim.autoReverse = true
      pulseCircleAnim.cycleCount = -1

      pulseCircleAnim.playFromStart()
    }
  }

  def activateTargetCountryAnim(): Unit ={
    if(!targetCountryAnimActive){

      println("Target Animation starting")
      targetCountryAnimActive = true

      this.children.add(targetCircleGroup)

      pulseCircleAnim.setNode(targetCircleGroup)
      pulseCircleAnim.byX = .3
      pulseCircleAnim.byY = .3
      pulseCircleAnim.autoReverse = true
      pulseCircleAnim.cycleCount = -1

      pulseCircleAnim.playFromStart()
    }
  }

  def deactivateAnimations(): Unit ={
    println("Deactivating Animations")

    if(sourceCountryAnimActive){
      sourceCountryAnimActive = false
      pulseCircleAnim.stop()
      this.children.removeAll(sourceCircleGroup)
    }

    if(targetCountryAnimActive){
      targetCountryAnimActive = false
      pulseCircleAnim.stop()
      this.children.removeAll(targetCircleGroup)
    }
  }
}