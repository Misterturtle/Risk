package GUI

import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.value.{ChangeListener, ObservableValue}

import scalafx.animation.TranslateTransition
import scalafx.beans.property.{DoubleProperty, ReadOnlyDoubleProperty}
import scalafx.scene.layout.{AnchorPane, VBox}
import scalafx.util.Duration

/**
  * Created by Harambe on 7/31/2017.
  */
trait DisplayConsole extends VBox {

  protected val descendAnim = new TranslateTransition(new Duration(1000), this)
  protected val ascendAnim = new TranslateTransition(new Duration(1000), this)
  private var _isDisplayed = false
  def isDisplayed:Boolean = _isDisplayed
  AnchorPane.setTopAnchor(this, 0)
  private val self = this

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

  def resizeYListener(sceneHeight: ReadOnlyDoubleProperty): ChangeListener[Number] = {
    new ChangeListener[Number] {
      override def changed(observable: ObservableValue[_ <: Number], oldValue: Number, newValue: Number): Unit = {
        AnchorPane.setBottomAnchor(self, sceneHeight.get * .8)
        if (!self.isDisplayed) {
          self.translateY.set(-self.height.value)
        }
      }
    }
  }

  def resizeXListener(sceneWidth: ReadOnlyDoubleProperty): ChangeListener[Number] ={
    new ChangeListener[Number] {
      override def changed(observable: ObservableValue[_ <: Number], oldValue: Number, newValue: Number): Unit = {
        AnchorPane.setLeftAnchor(self, sceneWidth.get * .4)
        AnchorPane.setRightAnchor(self, sceneWidth.get * .4)
      }
    }
  }

}
