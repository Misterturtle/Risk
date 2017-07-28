package GUI

import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.input.MouseEvent
import javafx.scene.layout.{CornerRadii, BackgroundFill, Background}

import Service._

import scalafx.geometry.Pos
import scalafx.scene.image.Image
import scalafx.scene.layout._
import scalafx.scene.paint.Color
import scalafx.scene.text.Text


class WorldMapUI(wmUICont: WorldMapUIController) extends AnchorPane {

  val polyXScale = new SimpleDoubleProperty()
  val polyYScale = new SimpleDoubleProperty()

  val shapeXScale = new SimpleDoubleProperty()
  shapeXScale.bind(width.delegate.divide(1920))
  val shapeYScale = new SimpleDoubleProperty()
  shapeYScale.bind(height.delegate.divide(1020))

  var countriesUI = initCountries(wmUICont.getCountries)
  val turnDisplay = new VBox()
  val turnDisplayText = new Text("Pre-Game")
  val phaseDisplayText = new Text("Pre-Game")

  val playerDisplay = new PlayerDisplayUI(wmUICont)
  playerDisplay.prefHeightProperty().bind(this.heightProperty().divide(10))
  playerDisplay.prefWidthProperty().bind(this.widthProperty().divide(4))
  children.add(playerDisplay)


  private def initTurnDisplay(): Unit = {
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

  private def updateTurnDisplay(wmUICont: WorldMapUIController): Unit = {

    val pName = wmUICont.getPlayersName

    wmUICont.getPhase match {
      case InitialPlacement =>
        turnDisplayText.text = pName
        phaseDisplayText.text = "Initial Placement - Armies Remaining: " + wmUICont.getPlayersArmies

      case TurnPlacement =>
        turnDisplayText.text = pName
        phaseDisplayText.text = "Turn Placement - Armies Remaining: " + wmUICont.getPlayersArmies

      case Attacking(s) =>
        turnDisplayText.text = pName
        phaseDisplayText.text = "Attacking"

      case Battle(s,t,pB,trans) =>
        turnDisplayText.text = pName
        phaseDisplayText.text = "Battling"

      case Reinforcement(s, t) =>
        turnDisplayText.text = pName
        phaseDisplayText.text = "Reinforcing"
    }

    turnDisplay.setBackground(new Background(new BackgroundFill(wmUICont.getPlayersColor, new CornerRadii(30, false), Insets.EMPTY)))
  }


  this.widthProperty().addListener(new ChangeListener[Number] {
    override def changed(observable: ObservableValue[_ <: Number], oldValue: Number, newValue: Number): Unit = {
      countriesUI.foreach { case (name, c) =>
        AnchorPane.setLeftAnchor(c, c.origPoints.minBy(_._1)._1 * polyXScale.get())
      }

      AnchorPane.setLeftAnchor(turnDisplay, (width.get / 2) - (width.get / 6))
    }
  })

  this.heightProperty().addListener(new ChangeListener[Number] {
    override def changed(observable: ObservableValue[_ <: Number], oldValue: Number, newValue: Number): Unit = {
      countriesUI.foreach { case (name, c) =>
        AnchorPane.setTopAnchor(c, c.origPoints.minBy(_._2)._2 * polyYScale.get)
      }
    }
  })

  private def initCountries(countries: List[Country]): Map[String, CountryUI] = {
    countries.map { c =>
      val ui = new CountryUI(c, PixelDatabase.lookup(c.name), wmUICont)
      this.children.add(ui)
      ui.initPoly(polyXScale, polyYScale)
      AnchorPane.setTopAnchor(ui, ui.origPoints.minBy(_._2)._2)
      AnchorPane.setLeftAnchor(ui, ui.origPoints.minBy(_._1)._1)
      (c.name, ui)
    }.toMap
  }


  private def scaleMap(): Unit = {
    val origImage = new Image("map.png")
    val origX = origImage.width.value
    val origY = origImage.height.value
    polyXScale.bind(width.delegate.divide(origX))
    polyYScale.bind(height.delegate.divide(origY))
  }

  private def enableDebug(): Unit = {
    this.setOnMouseClicked(new EventHandler[MouseEvent] {
      override def handle(event: MouseEvent): Unit = {
        println("(" + event.getX * (1 / polyXScale.get()) + "," + event.getY * (1 / polyYScale.get()) + "),")
      }
    })
  }

  def updateWorldMap(wmUICont: WorldMapUIController): Unit = {
    wmUICont.getCountries.foreach { c => countriesUI(c.name).update(c) }
    updateTurnDisplay(wmUICont)
    playerDisplay.update()
  }


  this.stylesheets.add("worldStyle.css")
  this.styleClass.add("worldMap")

  this.setOnMousePressed(new EventHandler[MouseEvent] {
    override def handle(event: MouseEvent): Unit = requestFocus()
  })


  scaleMap()
  initTurnDisplay()
  enableDebug()


}
