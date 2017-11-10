package GUI

import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.value.{ObservableValue, ChangeListener}
import javafx.scene.paint.Paint

import Service._

import scalafx.scene.Group
import scalafx.scene.layout._

import Sugar.CustomSugar._



class CountryUI(countryName:String, val countryDB:CountryDB, countryClickedInput: () => Unit, mapXScale:SimpleDoubleProperty, mapYScale:SimpleDoubleProperty) extends Group{

  private var highlightAllowed = true

  val ad = new SVGArmyDisplay(CountriesSVG.countryLookup(countryName)(), mapXScale, mapYScale)
  val animation = new CountryAnimation(mapXScale, mapYScale)

  val countryGroup = new Group(countryDB.container)
  countryDB.container.scaleX.bind(mapXScale)
  countryDB.container.scaleY.bind(mapYScale)
  countryDB.country.setOnMouseClicked(() => countryClickedInput())
  countryDB.country.setOnMouseEntered(() => countryDB.country.setFill(countryDB.highlightColor))
  countryDB.country.setOnMouseExited(() => countryDB.country.setFill(countryDB.baseColor))
  countryDB.country.toBack()
  countryDB.country.mouseTransparent = true

  this.mouseTransparent = true

  def update(newCountry:Country): Unit = {
    ad.update(newCountry.armies, newCountry.owner.map(_.color).getOrElse(CustomColors.gray))
  }

  def resizeXListener(): ChangeListener[Number] ={
    new ChangeListener[Number] {
      override def changed(observable: ObservableValue[_ <: Number], oldValue: Number, newValue: Number): Unit = {
        AnchorPane.setLeftAnchor(countryGroup, countryDB.origin._1 * mapXScale.get)
        AnchorPane.setLeftAnchor(ad, (countryDB.origin._1 + countryDB.adOrigin._1) * mapXScale.get)
        AnchorPane.setLeftAnchor(animation, (countryDB.origin._1 + countryDB.adOrigin._1) * mapXScale.get)
      }
    }
  }

  def resizeYListener(): ChangeListener[Number] = {
    new ChangeListener[Number] {
      override def changed(observable: ObservableValue[_ <: Number], oldValue: Number, newValue: Number): Unit = {
        AnchorPane.setTopAnchor(countryGroup, countryDB.origin._2 * mapYScale.get)
        AnchorPane.setTopAnchor(ad, (countryDB.origin._2 + countryDB.adOrigin._2) * mapYScale.get)
        AnchorPane.setTopAnchor(animation, (countryDB.origin._2 + countryDB.adOrigin._2) * mapYScale.get)
      }
    }
  }

  def highlight(): Unit ={
    if(highlightAllowed){
      countryDB.country.setFill(countryDB.highlightColor)
    }
  }

  def noHighlight(): Unit ={
    countryDB.country.setFill(countryDB.baseColor)
  }

  def enableInteractions(): Unit ={
    highlightAllowed = true
    countryDB.country.setOnMouseClicked(() => countryClickedInput())
  }

  def disableInteractions(): Unit ={
    highlightAllowed = false
    countryDB.country.setOnMouseClicked(() => {})
  }

  def activateSourceCountryAnim(color:Paint): Unit = {
    animation.activateSourceCountryAnim(color)
  }

  def activateTargetCountryAnim(color: Paint): Unit ={
    animation.activateTargetCountryAnim(color)
  }

  def deactivateAnimations(): Unit ={
    animation.deactivateAnimations()
  }
}




