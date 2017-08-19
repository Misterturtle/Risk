package Service

import GUI.{CustomColors, WorldMapUI}

import scala.collection.mutable.ListBuffer
import scalafx.beans.property.{ReadOnlyObjectProperty, ObjectProperty}

/**
  * Created by Harambe on 7/18/2017.
  */
trait Listener


trait PlayerListener extends Listener {
  protected val _player = new ObjectProperty[Player]()
  def bindPlayer(servicePlayer: ReadOnlyObjectProperty[Player]) = _player.bind(servicePlayer)
  def onPlayerChange(oldPlayer:Player, newPlayer:Player): () =>  Unit
}

trait CountryListener extends Listener {
  protected val _countries = new ObjectProperty[List[Country]]()
  def bindCountries(serviceCountries: ReadOnlyObjectProperty[List[Country]]) = _countries.bind(serviceCountries)
  def onCountryChange(oldCountries:List[Country], newCountries:List[Country]): () =>  Unit
}

trait PhaseListener extends Listener {
  protected val _phase = new ObjectProperty[Phase]
  def bindPhase(servicePhase: ReadOnlyObjectProperty[Phase]) = _phase.bind(servicePhase)
  def onPhaseChange(oldPhase:Phase, newPhase:Phase): () => Unit
}

class Service() {

  val sideEffectManager = new SideEffectManager(mutateWorldMapUnsafe)

  private val players = List[Player](HumanPlayer("Turtle", 1, 0, CustomColors.red), ComputerPlayer("Boy Wonder", 2, 0, CustomColors.blue), ComputerPlayer("Some Scrub", 3, 0, CustomColors.green))
  private val initWM = WorldMap(CountryFactory.getCountries, players, 0, NotInGame)
  private val _mutableWorldMap = new ObjectProperty[WorldMap]()
  _mutableWorldMap.set(initWM)
  def getWorldMapProperty: ReadOnlyObjectProperty[WorldMap] = _mutableWorldMap

  private val _activePlayer = new ObjectProperty[Player]()
  def getActivePlayer: ReadOnlyObjectProperty[Player] = _activePlayer
  private val playerListeners = new ListBuffer[PlayerListener]()

  private val _countries = new ObjectProperty[List[Country]]()
  def getCountries: ReadOnlyObjectProperty[List[Country]] = _countries
  private val countryListeners = new ListBuffer[CountryListener]()

  private val _phase = new ObjectProperty[Phase]()
  def getPhase: ReadOnlyObjectProperty[Phase] = _phase
  private val phaseListeners = new ListBuffer[PhaseListener]()

  def subscribePhaseListener(ln: PhaseListener): Unit = phaseListeners.append(ln)
  def subscribeCountryListener(ln: CountryListener): Unit = countryListeners.append(ln)
  def subscribePlayerListener(ln: PlayerListener): Unit = playerListeners.append(ln)


  def mutateWorldMapUnsafe(wm: WorldMap): Unit = {
    _mutableWorldMap.set(wm)
    updateProperties(wm)
  }

  def updateProperties(wm: WorldMap): Unit = {
    val changeList = new ListBuffer[() => Unit]()
    if (_activePlayer.value != wm.getActivePlayer.get) {
      playerListeners.foreach(l => changeList.append(l.onPlayerChange(_activePlayer.value, wm.getActivePlayer.get)))
      _activePlayer.delegate.setValue(wm.getActivePlayer.get)

    }

    if (_countries.value != wm.countries) {
      if(_countries.value == null)
        countryListeners.foreach(l => changeList.append(l.onCountryChange(Nil, wm.countries)))
      else
        countryListeners.foreach(l => changeList.append(l.onCountryChange(_countries.value, wm.countries)))

      _countries.delegate.setValue(wm.countries)
    }

    if (_phase.value != wm.phase) {
      phaseListeners.foreach(l => changeList.append(l.onPhaseChange(_phase.value, wm.phase)))
      _phase.delegate.setValue(wm.phase)
    }

    changeList.foreach(_())
    changeList.clear()
  }

  //Entry Point
  def begin() = sideEffectManager.performServiceEffect(Effects.begin(_mutableWorldMap.value))

}
