package GUI

import javafx.beans.binding.Bindings
import javafx.beans.property.{SimpleDoubleProperty, SimpleStringProperty}
import javafx.beans.value.{ObservableValue, ChangeListener}
import javafx.event.{ActionEvent, EventHandler}
import javafx.scene.layout.{CornerRadii, BackgroundFill, Background}
import javafx.scene.paint.Paint

import Service._

import scalafx.beans.property.ReadOnlyDoubleProperty
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Group
import scalafx.scene.layout.{StackPane, AnchorPane, VBox, HBox}
import scalafx.scene.paint.Color
import scalafx.scene.text.Text
import scalafx.util.Duration
import scalaz.Scalaz._

/**
  * Created by Harambe on 8/6/2017.
  */
class PlacementConsole(val sceneWidth: ReadOnlyDoubleProperty, val sceneHeight: ReadOnlyDoubleProperty, val windowXScale:SimpleDoubleProperty, val windowYScale: SimpleDoubleProperty) extends DisplayConsole {

  val self = this

  ascendAnim.duration = new Duration(270)
  descendAnim.duration = new Duration(270)

  var playerBeingDisplayed = "None"
  val countryBinding = new SimpleStringProperty()

  val phaseText = new Text()
  phaseText.setScaleX(3)
  phaseText.setScaleY(3)
  phaseText.styleClass.add("defaultText")

  val hintText = new Text()
  hintText.setScaleX(1.5)
  hintText.setScaleY(1.5)
  hintText.styleClass.add("defaultText")
  hintText.textProperty().bind(Bindings.concat("Select " , countryBinding))

  val phaseBox = new VBox()
  phaseBox.alignment = Pos.Center
  phaseBox.children.addAll(new Group(phaseText), new Group(hintText))
  phaseBox.padding = Insets(0,20,0,20)
  phaseBox.spacing = 10

  val contentGroup: Group = new Group(phaseBox)


  val layoutBox = new VBox()
  layoutBox.children.addAll(new Group(contentGroup))
  layoutBox.alignment = Pos.Center


  def checkAnimationUpdate(wmUICont:WorldMapUIController): Unit ={
    if(wmUICont.getCurrPlayersName != playerBeingDisplayed){
      ascendAnim.setOnFinished(new EventHandler[ActionEvent] {
        override def handle(event: ActionEvent): Unit = {
          update(wmUICont)
          descendAnim.setToY(0)
          openAnim()
        }
      })

      ascendAnim.setToY(-height.get)
      closeAnim()
    }
    else {
      update(wmUICont)
    }
  }


  def update(wmUICont:WorldMapUIController): Unit = {
    playerBeingDisplayed = wmUICont.getCurrPlayersName
    val phaseBG = new Background(new BackgroundFill(wmUICont.getCurrPlayersColor, new CornerRadii(0,0, 100, 100, false), javafx.geometry.Insets.EMPTY))
    layoutBox.setBackground(phaseBG)

    wmUICont.getPhase match {
      case InitialPlacement =>
        phaseText.setText("Country Grab")
        if(wmUICont.getCountries.forall(_.owner.nonEmpty)){
          countryBinding.setValue("a country you own")
          phaseText.setText("Army Placement")
        } else {
          phaseText.setText("Country Grab")
          countryBinding.setValue("an unowned country")
        }

      case TurnPlacement =>
        phaseText.setText("Army Placement")
        countryBinding.setValue("a country you own")

      case _ =>

    }
  }

  scaleContent(windowXScale, windowYScale)
  this.mouseTransparent = true
  this.children.addAll(layoutBox)
  this.alignment = Pos.TopCenter
}
