package GUI

import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.event.{ActionEvent, EventHandler}

import scalafx.animation.TranslateTransition
import scalafx.beans.property.{DoubleProperty, ReadOnlyDoubleProperty}
import scalafx.scene.Group
import scalafx.scene.layout.{AnchorPane, VBox}
import scalafx.util.Duration

import scalafx.scene.layout.HBox

/**
  * Created by Harambe on 8/8/2017.
  */
trait NextPhaseButton extends VBox {

    private val self = this

    val sceneWidth: ReadOnlyDoubleProperty
    val sceneHeight: ReadOnlyDoubleProperty
    val windowXScale: SimpleDoubleProperty
    val windowYScale: SimpleDoubleProperty
    val contentGroup: Group
    prefHeight.bind(sceneHeight.divide(10))
    prefWidth.bind(sceneWidth.divide(10))
    sceneHeight.addListener(resizeYListener(sceneHeight))
    sceneWidth.addListener(resizeXListener(sceneWidth))

    protected val descendAnim = new TranslateTransition(new Duration(1000), this)
    protected val ascendAnim = new TranslateTransition(new Duration(1000), this)

    private var _isDisplayed = false
    def isDisplayed:Boolean = _isDisplayed


    def scaleContent(windowXScale: SimpleDoubleProperty, windowYScale:SimpleDoubleProperty): Unit ={
      contentGroup.scaleX.bind(windowXScale)
      contentGroup.scaleY.bind(windowYScale)
    }


    def openAnim(): Unit ={
      println("Opened")
      _isDisplayed = true
      ascendAnim.setToY(0)
      ascendAnim.playFromStart()
    }

    def setCloseAnimOnFinished(action: () => Unit): Unit ={
      ascendAnim.onFinished = new EventHandler[ActionEvent] {
        override def handle(event: ActionEvent): Unit = action()
      }
    }

    def closeAnim(): Unit ={
      _isDisplayed = false
      descendAnim.setToY(this.prefHeight.get)
      descendAnim.playFromStart()
    }

    def resizeYListener(sceneHeight: ReadOnlyDoubleProperty): ChangeListener[Number] = {
      new ChangeListener[Number] {
        override def changed(observable: ObservableValue[_ <: Number], oldValue: Number, newValue: Number): Unit = {
          AnchorPane.setBottomAnchor(self, sceneHeight.get)
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

    def postInit(): Unit = {
      AnchorPane.setTopAnchor(this, sceneHeight.get - this.height.get)
      //translateY.delegate.setValue(height.value * -1)
    }
}
