package GUI

import javafx.beans.binding.DoubleBinding
import javafx.beans.property.{ReadOnlyObjectProperty, ReadOnlyDoubleProperty}
import javafx.scene.Scene

import scalafx.stage.Screen


/**
  * Created by Harambe on 8/16/2017.
  */

trait Scaleable{
  def bindPrefSize(sceneWidth: ReadOnlyDoubleProperty, sceneHeight: ReadOnlyDoubleProperty): Unit
  def bindScale(windowScaleX: DoubleBinding, windowScaleY: DoubleBinding): Unit
  def anchorResize(sceneWidth: ReadOnlyDoubleProperty, sceneHeight: ReadOnlyDoubleProperty): Unit
  def init(sceneWidth: ReadOnlyDoubleProperty, sceneHeight: ReadOnlyDoubleProperty): Unit
}

class Scaler(scene:Scene, nodesToScale: List[Scaleable]) {
  nodesToScale.foreach(_.bindPrefSize(scene.widthProperty(), scene.heightProperty()))
  scene.getRoot.layout()
  nodesToScale.foreach(_.bindScale(scene.widthProperty().divide(Screen.primary.bounds.getMaxX), scene.heightProperty().divide(Screen.primary.bounds.getMaxY)))
  nodesToScale.foreach(_.anchorResize(scene.widthProperty(), scene.heightProperty()))
  nodesToScale.foreach(_.init(scene.widthProperty(), scene.heightProperty()))

}
