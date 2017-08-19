
import GUI._
import Service._

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.layout.AnchorPane
import scalafx.scene.paint.Color

/**
  * Created by Harambe on 6/16/2017.
  */
object Main extends JFXApp {


  val root = new AnchorPane()

  stage = new PrimaryStage{
    scene = new Scene(root, 1200,800)
  }


  val service = new Service()
  val wmUICont = new WorldMapUIController(service.getWorldMapProperty, service.sideEffectManager)
  val wmUI = new WorldMapUI(wmUICont, wmUICont.receiveInput)
  val placeCons = new PlacementConsole()
  val attackCons:AttackConsole = new AttackConsole(placeCons.getTimeToFinish _, battleCons.getTimeToFinish _)
  val reinforcementCons:ReinforcementConsole = new ReinforcementConsole(attackCons.getTimeToFinish _, transferCons.getTimeToFinish _)
  val battleCons = new BattleConsole(attackCons.getTimeToFinish _, wmUICont.receiveInput)
  val transferCons = new TransferConsole(wmUICont.receiveInput, attackCons.getTimeToFinish _, battleCons.getTimeToFinish _, reinforcementCons.getTimeToFinish _)
  val endAttackButton = new EndAttackPhaseConsole(wmUICont.receiveInput)
  val endTurnButton = new EndTurnConsole(wmUICont.receiveInput, endAttackButton.getTimeToFinish _)


  val uiControllers = List[UIController](placeCons, attackCons, battleCons, transferCons, reinforcementCons, endAttackButton, endTurnButton)

  val playerListeners = List[PlayerListener](wmUI, placeCons, attackCons, reinforcementCons, endAttackButton, endTurnButton)
  playerListeners.foreach{_.bindPlayer(service.getActivePlayer)}
  playerListeners.foreach(service.subscribePlayerListener)

  val countryListeners = List[CountryListener](wmUI, placeCons)
  countryListeners.foreach{_.bindCountries(service.getCountries)}
  countryListeners.foreach(service.subscribeCountryListener)

  val phaseListeners = List[PhaseListener](wmUI, placeCons, attackCons, battleCons, transferCons, reinforcementCons, endAttackButton, endTurnButton)
  phaseListeners.foreach(_.bindPhase(service.getPhase))
  phaseListeners.foreach(service.subscribePhaseListener)


  stage.scene.value.setRoot(wmUI)
  uiControllers.foreach(wmUI.children.add(_))
  val scaler = new Scaler(stage.scene.value, uiControllers)

  service.begin()
}
