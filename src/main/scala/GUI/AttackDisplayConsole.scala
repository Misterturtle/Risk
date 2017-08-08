package GUI

import javafx.beans.property.{SimpleDoubleProperty, SimpleStringProperty}
import javafx.beans.value.{ObservableValue, ChangeListener}
import javafx.event.{ActionEvent, EventHandler}
import javafx.scene.layout.{CornerRadii, BackgroundFill, Background}

import Service._

import scalafx.beans.property.ReadOnlyDoubleProperty
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Group
import scalafx.scene.layout.{StackPane, AnchorPane, VBox, HBox}
import scalafx.scene.text.Text
import scalafx.util.Duration

/**
  * Created by Harambe on 8/6/2017.
  */
class AttackDisplayConsole(val sceneWidth: ReadOnlyDoubleProperty, val sceneHeight: ReadOnlyDoubleProperty, val windowXScale:SimpleDoubleProperty, val windowYScale: SimpleDoubleProperty) extends DisplayConsole {

  val self = this

  ascendAnim.duration = new Duration(270)
  descendAnim.duration = new Duration(270)

  private var sourceCountry: Option[Country] = None

  val phaseText = new Text("Test Test Test")
  phaseText.setScaleX(2)
  phaseText.setScaleY(2)
  phaseText.styleClass.add("defaultText")

  val phaseBox = new VBox()
  phaseBox.alignment = Pos.Center
  phaseBox.children.addAll(new Group(phaseText))
  phaseBox.padding = Insets(20)
  phaseBox.spacing = 10

  val contentGroup = new Group(phaseBox)

  val layoutBox = new VBox()
  layoutBox.children.addAll(new Group(contentGroup))
  layoutBox.alignment = Pos.Center


  def checkAnimationUpdate(wmUICont: WorldMapUIController): Unit = {

    wmUICont.getPhase match {

      case Attacking(s, _) if s != sourceCountry =>
        sourceCountry = s
        setCloseAnimOnFinished(() => {
          update(wmUICont)
          descendAnim.setToY(0)
          openAnim()
        })

        ascendAnim.setToY(-height.get)
        closeAnim()

      case _ =>
        setCloseAnimOnFinished(() => {})
        closeAnim()
    }
  }


    def update(wmUICont: WorldMapUIController): Unit = {
      val phaseBG = new Background(new BackgroundFill(wmUICont.getCurrPlayersColor, new CornerRadii(0, 0, 100, 100, false), javafx.geometry.Insets.EMPTY))
      layoutBox.setBackground(phaseBG)

      wmUICont.getPhase match {
        case Attacking(s, _) if s.isEmpty =>
          phaseText.setText("Select a source country to attack from")

        case Attacking(s, _) if s.nonEmpty =>
          phaseText.setText("Select a target country to attack")

        case _ =>

      }
    }

  scaleContent(windowXScale, windowYScale)
    this.mouseTransparent = true
    this.children.addAll(layoutBox)
    this.alignment = Pos.TopCenter
  }
