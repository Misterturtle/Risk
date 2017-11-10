package GUI

import javafx.animation.Interpolator
import javafx.beans.property.SimpleDoubleProperty
import javafx.scene.paint.Paint

import scalafx.animation.{RotateTransition, ScaleTransition}
import scalafx.geometry.Pos
import scalafx.scene.layout.{StackPane, VBox}
import scalafx.scene.shape.SVGPath
import scalafx.util.Duration

/**
  * Created by Harambe on 8/25/2017.
  */
class CountryAnimation(mapXScale:SimpleDoubleProperty, mapYScale:SimpleDoubleProperty) extends StackPane {

  val sourceCircleShape = new SVGPath()
  sourceCircleShape.setContent("M0.505,0.005c-0.276,0-0.5,0.224-0.5,0.5c0,0.276,0.224,0.5,0.5,0.5s0.5-0.224,0.5-0.5C1.005,0.229,0.781,0.005,0.505,0.005z M0.505,0.905c-0.221,0-0.4-0.179-0.4-0.4c0-0.221,0.179-0.4,0.4-0.4s0.4,0.179,0.4,0.4C0.905,0.726,0.726,0.905,0.505,0.905z")
  sourceCircleShape.setScaleX(70)
  sourceCircleShape.setScaleY(70)
  sourceCircleShape.mouseTransparent = true
  val sourceCircle = new VBox(sourceCircleShape)
  sourceCircle.alignment = Pos.Center
  sourceCircle.visible = false



  val targetCircleShape = new SVGPath()
  targetCircleShape.setContent("M1.079,0.555c-0.005-0.112-0.048-0.222-0.128-0.31l0.053-0.054L0.969,0.156L0.915,0.209c-0.088-0.08-0.198-0.123-0.31-0.128V0.005h-0.05v0.076C0.451,0.086,0.348,0.124,0.264,0.193L0.209,0.138L0.173,0.173l0.053,0.053C0.135,0.318,0.087,0.435,0.081,0.555H0.005v0.05h0.076c0.005,0.112,0.048,0.222,0.128,0.31L0.156,0.969l0.035,0.035l0.054-0.053c0.088,0.08,0.198,0.123,0.31,0.128v0.076h0.05V1.079c0.112-0.005,0.222-0.048,0.31-0.128l0.054,0.053l0.035-0.035L0.951,0.915c0.08-0.088,0.123-0.198,0.128-0.31h0.076v-0.05H1.079z M0.863,0.863c-0.156,0.156-0.41,0.156-0.566,0c-0.156-0.156-0.156-0.41,0-0.566c0.156-0.156,0.41-0.156,0.566,0C1.019,0.453,1.019,0.707,0.863,0.863z")
  targetCircleShape.setScaleX(70)
  targetCircleShape.setScaleY(70)
  targetCircleShape.mouseTransparent = true
  val targetCircle = new VBox(targetCircleShape)
  targetCircle.alignment = Pos.Center
  targetCircle.visible = false

  private var sourceCountryAnimActive = false
  private var targetCountryAnimActive = false
  private val pulseCircleAnim = new ScaleTransition(new Duration(1000))

  this.translateX.bind(width.delegate.divide(-2))
  this.translateY.bind(height.delegate.divide(-2))
  this.scaleX.bind(mapXScale)
  this.scaleY.bind(mapYScale)
  this.children.addAll(sourceCircle, targetCircle)


  def activateSourceCountryAnim(color:Paint): Unit = {
    sourceCircleShape.setFill(color)
    if(!sourceCountryAnimActive){

      sourceCountryAnimActive = true

      sourceCircle.visible = true
      this.toFront()

      pulseCircleAnim.setNode(sourceCircle)
      pulseCircleAnim.fromX = .9
      pulseCircleAnim.fromY = .9
      pulseCircleAnim.toX = 1.2
      pulseCircleAnim.toY = 1.2
      pulseCircleAnim.autoReverse = true
      pulseCircleAnim.cycleCount = -1

      pulseCircleAnim.playFromStart()
    }
  }

  def activateTargetCountryAnim(color: Paint): Unit ={
    targetCircleShape.setFill(color)
    if(!targetCountryAnimActive){
      targetCountryAnimActive = true
      targetCircle.visible = true
      this.toFront()

      val rotateCircleAnim = new RotateTransition()
      rotateCircleAnim.fromAngle = 0
      rotateCircleAnim.toAngle = 360
      rotateCircleAnim.duration = new Duration(3000)
      rotateCircleAnim.node = targetCircle
      rotateCircleAnim.cycleCount = -1
      rotateCircleAnim.setInterpolator(Interpolator.LINEAR)

      pulseCircleAnim.setNode(targetCircle)
      pulseCircleAnim.fromX = .9
      pulseCircleAnim.fromY = .9
      pulseCircleAnim.toX = 1.2
      pulseCircleAnim.toY = 1.2
      pulseCircleAnim.autoReverse = true
      pulseCircleAnim.cycleCount = -1

      pulseCircleAnim.playFromStart()
      rotateCircleAnim.playFromStart()
    }
  }

  def deactivateAnimations(): Unit ={
    if(sourceCountryAnimActive){
      sourceCountryAnimActive = false
      pulseCircleAnim.stop()
      sourceCircle.visible = false
    }

    if(targetCountryAnimActive){
      targetCountryAnimActive = false
      pulseCircleAnim.stop()
      targetCircle.visible = false
    }
  }


}
