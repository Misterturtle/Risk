//package GUI.CustomControls
//
//import java.lang.Boolean
//import javafx.beans.property.SimpleDoubleProperty
//import javafx.beans.value.{ChangeListener, ObservableValue}
//
//import scalafx.scene.shape.{PathElement, SVGPath}
//
///**
//  * Created by Rob on 7/29/2017.
//  */
//
//class SVGShape(val svgCommand: String) extends SVGPath {
//
//
//
//  def boundsInScene = this.localToScene(this.getBoundsInLocal)
//  def centerX = boundsInScene.getMaxX - (boundsInScene.getWidth / 2)
//  def centerY = boundsInScene.getMaxY - (boundsInScene.getHeight / 2)
//
//
//  def parseSVGCommand(): List[(Int, PathElement)] = {
//    val singleCommand = """(/)"""
//    svgCommand.filterNot(_ == " ")
//  }
//
//
//  def drawShape(): Unit ={
//    this.setContent(svgCommand)
//  }
//
//
//
//  this.parentProperty().getValue.needsLayoutProperty().addListener(new ChangeListener[Boolean] {
//    override def changed(observable: ObservableValue[_ <: Boolean], oldValue: Boolean, newValue: Boolean) = {
//      if(oldValue == true){
//        println("Detected a layout in SVGShape")
//        drawShape()
//
//      }
//    }
//  })
//
//
//
//
//
//
//
//
//}
//
