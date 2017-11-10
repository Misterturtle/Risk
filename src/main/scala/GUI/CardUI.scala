package GUI

import javafx.beans.value.{ObservableValue, ChangeListener}
import javafx.geometry.Insets
import javafx.scene.layout._
import javafx.scene.paint.Color

import Service.Card

import scalafx.geometry.Pos
import scalafx.scene.Group
import scalafx.scene.image.ImageView
import scalafx.scene.layout.{StackPane, VBox}
import scalafx.scene.text.Text

import Sugar.CustomSugar._
import scalaz.Scalaz._

/**
  * Created by Harambe on 8/26/2017.
  */
class CardUI(val card:Card) extends StackPane {

  private val nameContainer = new VBox()
  private val name = new Text(card.countryName)
  private def nameYScale = (prefNameX < prefNameY) ? prefNameX | prefNameY
  private def nameXScale = (prefNameY < prefNameX) ? prefNameY | prefNameX
  private def prefNameX = (prefWidth.value * .8) / name.boundsInLocal.value.getMaxX
  private def prefNameY = (prefHeight.value / 3) / name.boundsInLocal.value.getMaxY
  nameContainer.prefHeight.bind(this.prefHeight.delegate.divide(3))
  nameContainer.prefWidth.bind(this.prefWidth)
  nameContainer.alignment = Pos.Center
  nameContainer.children.add(new Group(name))
  nameContainer.toFront()

  private val countryContainer = new VBox()
  private val country = CountriesSVG.countryLookup(card.countryName)().country
  private def countryYScale = (prefCountryX < prefCountryY) ? prefCountryX | prefCountryY
  private def countryXScale = (prefCountryY < prefCountryX) ? prefCountryY | prefCountryX
  private def prefCountryX = (prefWidth.value / country.boundsInLocal.value.getWidth) *   .9
  private def prefCountryY = (prefHeight.value / 3) / country.boundsInLocal.value.getHeight



  country.setOnMouseClicked(()=> {})
  country.setOnMouseEntered(()=> {})
  country.setOnMouseExited(()=> {})
  countryContainer.prefHeight.bind(this.prefHeight.delegate.divide(3))
  countryContainer.prefWidth.bind(this.prefWidth)
  countryContainer.children.add(new Group(country))
  countryContainer.alignment = Pos.Center
  countryContainer.toFront()

  private val armyTypeContainer = new VBox()
  private val armyImage = new ImageView(card.armyType.image)
  private def armyFitY = (prefArmyX < prefArmyY) ? prefArmyX | prefArmyY
  private def armyFitX = (prefArmyY < prefArmyX) ? prefArmyY | prefArmyX
  private def prefArmyX = prefWidth.value * .8
  private def prefArmyY = prefHeight.value * .33

  armyImage.preserveRatio = true
  armyTypeContainer.prefHeight.bind(this.prefHeight.delegate.divide(3))
  armyTypeContainer.prefWidth.bind(this.prefWidth)
  armyTypeContainer.children.add(new Group(armyImage))
  armyTypeContainer.alignment = Pos.Center
  armyTypeContainer.toFront()

  this.prefWidth.addListener(new ChangeListener[Number] {
    override def changed(observable: ObservableValue[_ <: Number], oldValue: Number, newValue: Number): Unit = {
      println("X: "+prefCountryX)
      println("Y: "+prefCountryY)
      country.scaleX = countryXScale
      name.scaleX = nameXScale
      armyImage.fitHeight = armyFitY
    }
  })

  this.prefHeight.addListener(new ChangeListener[Number] {
    override def changed(observable: ObservableValue[_ <: Number], oldValue: Number, newValue: Number): Unit = {
      println("X: "+prefCountryX)
      println("Y: "+prefCountryY)
      country.scaleY = countryYScale
      name.scaleY = nameYScale
      armyImage.fitWidth = armyFitX
    }
  })


  private val layoutBox = new VBox()
  layoutBox.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(10), new BorderWidths(10), new Insets(-5))))
  layoutBox.children.addAll(nameContainer, countryContainer, armyTypeContainer)
  layoutBox.spacing = 5
  private val bg = new Background(new BackgroundFill(Color.WHITE, new CornerRadii(10), Insets.EMPTY))
  layoutBox.setBackground(bg)
  layoutBox.alignment = Pos.Center
  private val shadeBox = new VBox()
  shadeBox.setBackground(new Background(new BackgroundFill(Color.color(0,0,0,.4), new CornerRadii(10), Insets.EMPTY)))
  shadeBox.visible = false




  this.mouseTransparent = true
  this.alignment = Pos.Center
  this.children.addAll(layoutBox, shadeBox)

  def shadeCard() = shadeBox.visible = true
  def hideShadeCard() = shadeBox.visible = false


  def init(): Unit ={
    country.scaleX = countryXScale
    country.scaleY = countryYScale
    name.scaleX = nameXScale
    name.scaleY = nameYScale
    armyImage.fitWidth = armyFitX
    armyImage.fitHeight = armyFitY
  }

}
