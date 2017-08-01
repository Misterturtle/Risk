package GUI

import scalafx.animation.TranslateTransition
import scalafx.scene.layout.VBox
import scalafx.util.Duration

/**
  * Created by Harambe on 7/31/2017.
  */
trait DisplayConsole extends VBox {

  private val descendAnim = new TranslateTransition(new Duration(1000), this)
  private val ascendAnim = new TranslateTransition(new Duration(1000), this)
  private var _isDisplayed = false
  def isDisplayed:Boolean = _isDisplayed

  def openAnim(): Unit ={
    println("Descending Console. Height is: "+this.height.value)
    _isDisplayed = true
    descendAnim.byY = this.height.value
    descendAnim.playFromStart()
  }

  def closeAnim(): Unit ={
    println("Ascending Console")
    _isDisplayed = false
    ascendAnim.byY = -this.height.value
    ascendAnim.playFromStart()
  }
}
