package GUI

import java.lang.Boolean
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.event.EventHandler
import javafx.scene.input.MouseEvent

import Service._

import scalafx.scene.Group
import scalafx.scene.image.Image
import scalafx.scene.layout._


class WorldMapUI(wmUICont: WorldMapUIController) extends AnchorPane {

  val polyXScale = new SimpleDoubleProperty()
  val polyYScale = new SimpleDoubleProperty()

  val shapeXScale = new SimpleDoubleProperty()
  shapeXScale.bind(width.delegate.divide(1920))
  val shapeYScale = new SimpleDoubleProperty()
  shapeYScale.bind(height.delegate.divide(1020))

  var countriesUI = initCountries(wmUICont.getCountries)
  val battleDisplayConsole = new BattleDisplayConsole(wmUICont)
  battleDisplayConsole.prefHeight.bind(this.heightProperty().divide(4))
  battleDisplayConsole.prefWidth.bind(this.widthProperty().divide(10))
  battleDisplayConsole.contentGroup.scaleX.bind(shapeXScale)
  battleDisplayConsole.contentGroup.scaleY.bind(shapeYScale)


  val transferDisplayConsole = new TransferDisplayConsole

  val playerDisplay = new PlayerDisplayUI(wmUICont)
  playerDisplay.prefHeightProperty().bind(this.heightProperty().divide(10))
  playerDisplay.prefWidthProperty().bind(this.widthProperty().divide(4))



  private def updateDisplayConsole(wmUICont: WorldMapUIController): Unit = {
    wmUICont.getPhase match {

      case Battle(_, _, _, trans) if trans =>
          if (battleDisplayConsole.isDisplayed)
            battleDisplayConsole.endBattle()
          if (!transferDisplayConsole.isDisplayed){
            transferDisplayConsole.openAnim()
            transferDisplayConsole.update()
          }


      case Battle(s, t, pB, trans) =>
        if (!battleDisplayConsole.isDisplayed) {
          battleDisplayConsole.startBattle(s,t)
        }
        battleDisplayConsole.update(s, t)

      case _ =>

    }
  }


  this.widthProperty().addListener(new ChangeListener[Number] {
    override def changed(observable: ObservableValue[_ <: Number], oldValue: Number, newValue: Number): Unit = {
      countriesUI.foreach { case (name, c) =>
        AnchorPane.setLeftAnchor(c, c.origPoints.minBy(_._1)._1 * polyXScale.get())
      }

      AnchorPane.setLeftAnchor(battleDisplayConsole, width.get * .4)
      AnchorPane.setRightAnchor(battleDisplayConsole, width.get * .4)

    }
  })

  this.heightProperty().addListener(new ChangeListener[Number] {
    override def changed(observable: ObservableValue[_ <: Number], oldValue: Number, newValue: Number): Unit = {
      countriesUI.foreach { case (name, c) =>
        AnchorPane.setTopAnchor(c, c.origPoints.minBy(_._2)._2 * polyYScale.get)
      }

      AnchorPane.setTopAnchor(battleDisplayConsole, 0)
      AnchorPane.setBottomAnchor(battleDisplayConsole, height.value * .8)

      if(!battleDisplayConsole.isDisplayed){
        battleDisplayConsole.translateY.set(-battleDisplayConsole.height.value)
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
    wmUICont.getCountries.foreach { c => countriesUI(c.name).update() }
    updateDisplayConsole(wmUICont)
    playerDisplay.update()
  }


  this.stylesheets.add("worldStyle.css")
  this.styleClass.add("worldMap")

  this.setOnMousePressed(new EventHandler[MouseEvent] {
    override def handle(event: MouseEvent): Unit = requestFocus()
  })

  def postInit(): Unit ={
    this.layout()
    battleDisplayConsole.postInit()
  }





  scaleMap()
  enableDebug()
  children.addAll(playerDisplay, battleDisplayConsole, transferDisplayConsole)

}
