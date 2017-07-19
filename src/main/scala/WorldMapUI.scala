import javafx.beans.binding.Bindings

import scalafx.geometry.Pos
import scalafx.scene.layout._
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.event.EventHandler
import javafx.scene.input.MouseEvent

import scala.util.Random
import scalafx.scene.image.Image
import scalafx.scene.paint.Color
import scalafx.scene.text.Text


class WorldMapUI(wm: WorldMap, wmInputHandler:WorldMapInputHandler) extends AnchorPane {

  val bgXScale = new SimpleDoubleProperty()
  val bgYScale = new SimpleDoubleProperty()

  var countriesUI = initCountries(wm.countries)
  val turnDisplay = new VBox()
  val turnDisplayText = new Text("Pre-Game")
  val phaseDisplayText = new Text("Pre-Game")



  private def initTurnDisplay(): Unit ={
    val turnDisplayFontSize = new SimpleDoubleProperty()
    turnDisplayFontSize.bind(this.widthProperty().add(this.heightProperty()).divide(130))
    val phaseDisplayFontSize = new SimpleDoubleProperty()
    phaseDisplayFontSize.bind(this.widthProperty().add(this.heightProperty()).divide(170))

    turnDisplay.styleClass.add(0, "turnDisplay")
    turnDisplay.styleClass.add(1, "grayBackground")
    turnDisplay.alignment = Pos.Center
    turnDisplay.children.addAll(turnDisplayText, phaseDisplayText)

    turnDisplayText.fill = Color.White
    turnDisplayText.styleProperty().bind(Bindings.concat("-fx-font-size: ", turnDisplayFontSize.asString(), ";", "-fx-text-: white; -fx-font-weight: bolder;"))

    phaseDisplayText.fill = Color.White
    phaseDisplayText.styleProperty().bind(Bindings.concat("-fx-font-size: ", phaseDisplayFontSize.asString(), ";", "-fx-text-: white; -fx-font-weight: bolder;"))

    turnDisplay.prefWidthProperty().bind(this.widthProperty().divide(3))
    turnDisplay.prefHeightProperty().bind(this.heightProperty().divide(30))

    this.children.add(turnDisplay)
  }

  private def updateTurnDisplay(player:Option[Player], phase:Phase): Unit ={

    phase match{
      case InitialPlacement =>
        turnDisplayText.text = player.map(_.name).getOrElse("Waiting")
        phaseDisplayText.text = "Initial Placement - Armies Remaining: " + player.map(_.armies).getOrElse(-1)

      case TurnPlacement =>
        turnDisplayText.text = player.map(_.name).getOrElse("Waiting")
        phaseDisplayText.text = "Turn Placement - Armies Remaining: " + player.map(_.armies).getOrElse(-1)

      case Attacking =>
        turnDisplayText.text = player.map(_.name).getOrElse("Waiting")
        phaseDisplayText.text = "Attacking"

      case Reinforcement =>
        turnDisplayText.text = player.map(_.name).getOrElse("Waiting")
        phaseDisplayText.text = "Reinforcing"
    }

    turnDisplay.styleClass.remove(1)
    turnDisplay.styleClass.add(1, player.map(_.color).getOrElse("gray")+"Background")
  }



  this.widthProperty().addListener(new ChangeListener[Number] {
    override def changed(observable: ObservableValue[_ <: Number], oldValue: Number, newValue: Number): Unit = {
      countriesUI.foreach {case (name, c) =>
        AnchorPane.setLeftAnchor(c, c.origPoints.minBy(_._1)._1 * bgXScale.get())
        c.resizePoly()
      }

      AnchorPane.setLeftAnchor(turnDisplay, (width.get / 2) - (width.get / 6))
    }
  })

  this.heightProperty().addListener(new ChangeListener[Number] {
    override def changed(observable: ObservableValue[_ <: Number], oldValue: Number, newValue: Number): Unit = {
      countriesUI.foreach {case  (name,c) =>
        AnchorPane.setTopAnchor(c, c.origPoints.minBy(_._2)._2 * bgYScale.get)
        c.resizePoly()
      }
    }
  })

  private def initCountries(countries: List[Country]): Map[String,CountryUI] = {
    countries.map { c =>
      val ui = new CountryUI(c, PixelDatabase.lookup(c.name), wmInputHandler)
      this.children.add(ui)
      ui.initShape(bgXScale, bgYScale)
      AnchorPane.setTopAnchor(ui, ui.origPoints.minBy(_._2)._2 * bgYScale.get())
      AnchorPane.setLeftAnchor(ui, ui.origPoints.minBy(_._1)._1 * bgXScale.get())
      ui.resizePoly()
      (c.name, ui)
    }.toMap
  }

  private def styleMap(): Unit = {
    this.stylesheets.add("worldStyle.css")
    this.styleClass.add("worldMap")
  }


  private def scaleMap(): Unit = {
    val origImage = new Image("map.jpg")
    val origX = origImage.width.value
    val origY = origImage.height.value
    bgXScale.bind(width.delegate.divide(origX))
    bgYScale.bind(height.delegate.divide(origY))
  }


  private def enableDebug(): Unit = {
    this.setOnMouseClicked(new EventHandler[MouseEvent] {
      override def handle(event: MouseEvent): Unit = {
        println("(" + event.getX * (1 / bgXScale.get()) + "," + event.getY * (1 / bgYScale.get()) + "),")
      }
    })


  }

  def updateWorldMap(worldMap:WorldMap, wmInputHandler: WorldMapInputHandler): Unit = {
    worldMap.countries.foreach{c => countriesUI(c.name).update(c)}
    updateTurnDisplay(worldMap.getActivePlayer, worldMap.phase)
  }

  styleMap()
  scaleMap()
  initTurnDisplay()
  enableDebug()
}
