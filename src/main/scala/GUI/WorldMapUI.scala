package GUI

import java.lang.Boolean
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.event.{ActionEvent, EventHandler}
import javafx.scene.input.MouseEvent

import Service._

import scalafx.scene.image.Image
import scalafx.scene.layout._
import scalafx.stage.Screen
import scalaz.Scalaz._


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

  val placementDisplayConsole = new PlacementConsole(width, height, windowXScale, windowYScale)
  val attackDisplayConsole = new AttackDisplayConsole(width, height, windowXScale, windowYScale)
  val battleDisplayConsole = new BattleDisplayConsole(wmUICont,width, height, windowXScale, windowYScale)
  val transferDisplayConsole = new TransferDisplayConsole(wmUICont,width, height, windowXScale, windowYScale)

  val endAttackPhaseButton = new EndAttackPhaseButton(width, height, windowXScale, windowYScale)


  val playerDisplay = new PlayerDisplayUI(wmUICont)
  playerDisplay.prefHeightProperty().bind(this.heightProperty().divide(10))
  playerDisplay.prefWidthProperty().bind(this.widthProperty().divide(4))
  playerDisplay.statsBar.scaleX.bind(windowXScale)
  playerDisplay.statsBar.scaleY.bind(windowYScale)

  this.stylesheets.add("worldStyle.css")
  this.styleClass.add("worldMap")

  this.setOnMousePressed(new EventHandler[MouseEvent] {
    override def handle(event: MouseEvent): Unit = requestFocus()
  })
  children.addAll(playerDisplay, placementDisplayConsole, attackDisplayConsole, battleDisplayConsole, transferDisplayConsole, endAttackPhaseButton)


  private def updateDisplayConsole(wmUICont: WorldMapUIController): Unit = {
    wmUICont.getPhase match {
      case InitialPlacement =>
        placementDisplayConsole.checkAnimationUpdate(wmUICont)

      case TurnPlacement =>
        if (!placementDisplayConsole.isDisplayed)
          placementDisplayConsole.openAnim()

        placementDisplayConsole.checkAnimationUpdate(wmUICont)

      case Attacking(_, _) =>
        if (placementDisplayConsole.isDisplayed) {
          placementDisplayConsole.setCloseAnimOnFinished(() => {
            attackDisplayConsole.update(wmUICont)
            attackDisplayConsole.openAnim()
          })
          placementDisplayConsole.closeAnim()
        } else {
          attackDisplayConsole.checkAnimationUpdate(wmUICont)
        }


        endAttackPhaseButton.checkAnimationUpdate(wmUICont)


      case Battle(s, t, _, trans) if trans =>
        if (battleDisplayConsole.isDisplayed) {
          battleDisplayConsole.endBattle()
          battleDisplayConsole.setCloseAnimOnFinished(() => {
            transferDisplayConsole.setCloseAnimOnFinished(() => attackDisplayConsole.checkAnimationUpdate(wmUICont))
            transferDisplayConsole.startTransfer(s, t)
          })
        }

      case Battle(s, t, pB, trans) =>
        if (!battleDisplayConsole.isDisplayed) {
          attackDisplayConsole.setCloseAnimOnFinished(()=>{})
          attackDisplayConsole.closeAnim()
          endAttackPhaseButton.checkAnimationUpdate(wmUICont)
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


  def updateWorldMap(wmUICont: WorldMapUIController): Unit = {
    wmUICont.getCountries.foreach { c => countriesUI(c.name).update() }
    updateDisplayConsole(wmUICont)
    (playerDisplay.nameText.getText != wmUICont.getCurrPlayersName) ? playerDisplay.closeAnim() | playerDisplay.update()
  }

  def postInit(): Unit = {
    this.layout()
    battleDisplayConsole.postInit()
    transferDisplayConsole.postInit()
    placementDisplayConsole.postInit()
    attackDisplayConsole.postInit()
    endAttackPhaseButton.postInit()
  }
}
