package GUI

import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.event.{ActionEvent, EventHandler}

import scalafx.animation.TranslateTransition
import scalafx.beans.property.{DoubleProperty, ReadOnlyDoubleProperty}
import scalafx.scene.Group
import scalafx.scene.layout.{AnchorPane, VBox}
import scalafx.util.Duration

trait DisplayConsole extends VBox {

  private val self = this

  val sceneWidth: ReadOnlyDoubleProperty
  val sceneHeight: ReadOnlyDoubleProperty
  val windowXScale: SimpleDoubleProperty
  val windowYScale: SimpleDoubleProperty
  val contentGroup: Group
  prefHeight.bind(sceneHeight.divide(4))
  prefWidth.bind(sceneWidth.divide(10))
  sceneHeight.addListener(resizeYListener(sceneHeight))
  sceneWidth.addListener(resizeXListener(sceneWidth))

  protected val descendAnim = new TranslateTransition(new Duration(1000), this)
  protected val ascendAnim = new TranslateTransition(new Duration(1000), this)

  private var _isDisplayed = false
  def isDisplayed:Boolean = _isDisplayed

  AnchorPane.setTopAnchor(this, 0)


  def scaleContent(windowXScale: SimpleDoubleProperty, windowYScale:SimpleDoubleProperty): Unit ={
    contentGroup.scaleX.bind(windowXScale)
    contentGroup.scaleY.bind(windowYScale)
  }


  def openAnim(): Unit ={
    _isDisplayed = true
    descendAnim.setToY(0)
    descendAnim.playFromStart()
  }

  def setCloseAnimOnFinished(action: () => Unit): Unit ={
    ascendAnim.onFinished = new EventHandler[ActionEvent] {
      override def handle(event: ActionEvent): Unit = action()
    }
  }

  def closeAnim(): Unit ={
    _isDisplayed = false
    ascendAnim.setToY(-this.height.get)
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
        AnchorPane.setLeftAnchor(self, sceneWidth.get * .3)
        AnchorPane.setRightAnchor(self, sceneWidth.get * .3)
      }
    }
  }

  def postInit(): Unit = {
    translateY.delegate.setValue(height.value * -1)
  }
}
