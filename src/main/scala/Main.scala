
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
    scene = new Scene(root, 1281.6, 851.52)
  }

  val servInpHand = new ServiceInputHandler(service.getWorldMapCopy, service.getSideEffectManager)
  val service = new Service(servInpHand)
  val wmUI = new WorldMapUI(servInpHand.receiveInput)
  val placeCons = new PlacementConsole()
  val attackCons:AttackConsole = new AttackConsole(placeCons.getTimeToFinish _, battleCons.getTimeToFinish _)
  val reinforcementCons:ReinforcementConsole = new ReinforcementConsole(attackCons.getTimeToFinish _, transferCons.getTimeToFinish _)
  val battleCons = new BattleConsole(attackCons.getTimeToFinish _, servInpHand.receiveInput)
  val transferCons = new TransferConsole(servInpHand.receiveInput, attackCons.getTimeToFinish _, battleCons.getTimeToFinish _, reinforcementCons.getTimeToFinish _)
  val endAttackButton = new EndAttackPhaseDisplay(servInpHand.receiveInput)
  val endTurnButton = new EndTurnDisplay(servInpHand.receiveInput, endAttackButton.getTimeToFinish _)
  val cardCollectionDisplay = new CardCollectionDisplay(servInpHand.receiveInput, wmUI.enableWMInteractions, wmUI.disableWMInteractions)
  val cardClaimDisplay = new CardClaimDisplay(endTurnButton.getTimeToFinish _, cardCollectionDisplay)
  val playerDisplay = new PlayerDisplayUI()


  val uiControllers = List[UIController](placeCons, attackCons, battleCons, transferCons, reinforcementCons, endAttackButton, endTurnButton, playerDisplay, cardClaimDisplay, cardCollectionDisplay)

  val playerListeners = List[PlayerListener](wmUI, placeCons, attackCons, reinforcementCons, endAttackButton, endTurnButton, playerDisplay, cardClaimDisplay)
  playerListeners.foreach{_.bindPlayer(service.getActivePlayer)}
  playerListeners.foreach(service.subscribePlayerListener)

  val countryListeners = List[CountryListener](wmUI, placeCons, playerDisplay)
  countryListeners.foreach{_.bindCountries(service.getCountries)}
  countryListeners.foreach(service.subscribeCountryListener)

  val phaseListeners = List[PhaseListener](wmUI, placeCons, attackCons, battleCons, transferCons, reinforcementCons, endAttackButton, endTurnButton, cardClaimDisplay)
  phaseListeners.foreach(_.bindPhase(service.getPhase))
  phaseListeners.foreach(service.subscribePhaseListener)

  val deckListeners = List[DeckListener](cardClaimDisplay)
  deckListeners.foreach(_.bindDeck(service.getDeck))
  deckListeners.foreach(service.subscribeDeckListener)



  stage.scene.value.setRoot(wmUI)
  uiControllers.foreach(wmUI.children.add(_))
  val scaler = new Scaler(stage.scene.value, uiControllers)

  service.begin()
}
