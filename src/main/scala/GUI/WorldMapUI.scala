package GUI

import java.lang.Boolean
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.event.{ActionEvent, EventHandler}
import javafx.scene.input.MouseEvent

import Service._

import scalafx.animation.PauseTransition
import scalafx.scene.Group
import scalafx.scene.image.Image
import scalafx.scene.layout._
import scalafx.stage.Screen
import scalafx.util.Duration


class WorldMapUI(wmUICont: WorldMapUIController) extends AnchorPane {

  private val self = this
  val mapImage = new Image("map.png")
  val mapImageXScale = new SimpleDoubleProperty()
  mapImageXScale.bind(width.delegate.divide(mapImage.width.value))
  val mapImageYScale = new SimpleDoubleProperty()
  mapImageYScale.bind(height.delegate.divide(mapImage.height.value))

  val windowXScale = new SimpleDoubleProperty()
  windowXScale.bind(width.delegate.divide(Screen.primary.bounds.getMaxX))
  val windowYScale = new SimpleDoubleProperty()
  windowYScale.bind(height.delegate.divide(Screen.primary.bounds.getMaxY - 20))

  var countriesUI = initCountries(wmUICont.getCountries)
  countriesUI.foreach { case (name, coun) =>
    this.width.addListener(coun.resizeXListener(mapImageXScale))
    this.height.addListener(coun.resizeYListener(mapImageYScale))
  }

  val battleDisplayConsole = new BattleDisplayConsole(wmUICont)
  battleDisplayConsole.prefHeight.bind(this.heightProperty().divide(4))
  battleDisplayConsole.prefWidth.bind(this.widthProperty().divide(10))
  this.width.addListener(battleDisplayConsole.resizeXListener(width))
  this.height.addListener(battleDisplayConsole.resizeYListener(height))
  battleDisplayConsole.contentGroup.scaleX.bind(windowXScale)
  battleDisplayConsole.contentGroup.scaleY.bind(windowYScale)

  val transferDisplayConsole = new TransferDisplayConsole(wmUICont)

  val playerDisplay = new PlayerDisplayUI(wmUICont)
  playerDisplay.prefHeightProperty().bind(this.heightProperty().divide(10))
  playerDisplay.prefWidthProperty().bind(this.widthProperty().divide(4))

  this.stylesheets.add("worldStyle.css")
  this.styleClass.add("worldMap")

  this.setOnMousePressed(new EventHandler[MouseEvent] {
    override def handle(event: MouseEvent): Unit = requestFocus()
  })
  enableDebug()
  children.addAll(playerDisplay, battleDisplayConsole, transferDisplayConsole)


  private def updateDisplayConsole(wmUICont: WorldMapUIController): Unit = {
    wmUICont.getPhase match {
      case Battle(s, t, _, trans) if trans =>
        if (battleDisplayConsole.isDisplayed)
          battleDisplayConsole.endBattle()
        if (!transferDisplayConsole.isDisplayed) {
          transferDisplayConsole.openAnim()
          transferDisplayConsole.update(s, t)
        }

      case Battle(s, t, pB, trans) =>
        if (!battleDisplayConsole.isDisplayed) {
          battleDisplayConsole.startBattle(s, t)
        }
        battleDisplayConsole.update(s, t, pB)

      case _ =>
    }
  }

  private def initCountries(countries: List[Country]): Map[String, CountryUI] = {
    countries.map { c =>
      val ui = new CountryUI(c, PixelDatabase.lookup(c.name), wmUICont)
      this.children.add(ui)
      ui.initPoly(mapImageXScale, mapImageYScale)
      AnchorPane.setTopAnchor(ui, ui.origPoints.minBy(_._2)._2)
      AnchorPane.setLeftAnchor(ui, ui.origPoints.minBy(_._1)._1)
      (c.name, ui)
    }.toMap
  }


  private def enableDebug(): Unit = {
    this.setOnMouseClicked(new EventHandler[MouseEvent] {
      override def handle(event: MouseEvent): Unit = {
        println("mapScale Pixels: (" + event.getX * (1 / mapImageXScale.get()) + "," + event.getY * (1 / mapImageYScale.get()) + "),")
        println("screenScale Pixels: (" + event.getX + "," + event.getY + ")")
      }
    })
  }

  def updateWorldMap(wmUICont: WorldMapUIController): Unit = {
    wmUICont.getCountries.foreach { c => countriesUI(c.name).update() }
    updateDisplayConsole(wmUICont)
    playerDisplay.update()
  }

  def postInit(): Unit = {
    this.layout()
    battleDisplayConsole.postInit()
  }
}
