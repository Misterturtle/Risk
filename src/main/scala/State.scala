import CustomTypes.EndTurnFunction

/**
  * Created by Harambe on 6/16/2017.
  */

trait Transition {
  val conditions: List[() => Boolean]
}

case class ForwardTransition(conditions: List[() => Boolean], forwardTransState: ()=>State) extends Transition

case class ReturnTransition(conditions: List[() => Boolean]) extends Transition

abstract class State(protected val lm: LogicMachine = new LogicMachine()) {

  protected var _forwardState: Option[()=>State] = None
  protected var _returnState: Boolean = false

  def forwardState: Option[()=>State] = _forwardState

  val transitions: List[Transition]

  def returnState: Boolean = _returnState

  def update(): Unit = {
    checkReactivation()
    _forwardState match {
      case Some(state) =>
        state().update()

      case None =>
        checkTransitions()
    }
  }

  private def checkReactivation(): Unit = {
    if (_forwardState.exists(_().returnState)) {
      _forwardState.get()._returnState = false
      _forwardState = None
    }

  }

  private def checkTransitions(): Unit = {
    transitions.find(_.conditions.forall(_ ())) match {
      case Some(forwardTrans: ForwardTransition) =>
        _forwardState = Some(forwardTrans.forwardTransState)

      case Some(returnTrans: ReturnTransition) =>
        _returnState = true

      case None =>
        lm.update()
    }
  }
}


class TestState(val transitions: List[Transition], lm: LogicMachine = new LogicMachine) extends State(lm)

case class WorldMapState(initPlaceComp: () => Boolean, players: List[Player], countries: Map[String, Country]) extends State() {


  val transToInitPlace = ForwardTransition(List(() => !initPlaceComp()), ()=>InitPlaceState(players, countries))
  val transitions = List[Transition](transToInitPlace)
}

case class InitPlaceState(players: List[Player], countries: Map[String, Country]) extends State {

  def activePlayer = _activePlayer

  private var _activePlayer = players.head
  private var _setupComplete = false

  def setup(): Unit = {
    val startingArmies = 35 - (players.size - 3) * 5
    players.foreach {
      _.addAvailableArmies(startingArmies)
    }


    _setupComplete = true
  }

  val endPlayersTurn: EndTurnFunction = ()=>{
    countries.foreach(_._2.setClickAction(() => {}))

    val nextPlayer = players.find(_.playerNumber == activePlayer.playerNumber + 1)
    nextPlayer match {
      case Some(player) =>
        _activePlayer = player
      case None =>
        _activePlayer = players.head
    }
  }

  def isCountryOwned(country: Country): Boolean = country.owner.nonEmpty

  def setNonOwnedCountriesOnClickToPlaceArmy() = {
    countries.foreach { case (name, country) =>
      if (!isCountryOwned(country)) {
        country.setClickAction(() => {
          country.setOwner(activePlayer)
          country.addArmies(1)
          activePlayer.removeAvailableArmies(1)
          endPlayersTurn()
        }
        )
      }
    }
  }


  def areAllCountriesOwned = countries.forall(_._2.owner.nonEmpty)

  def setOwnedCountriesOnClickToPlaceArmy() = {
    countries.foreach { case (name, country) =>
      if (country.owner.getOrElse(new HumanPlayer()) == activePlayer) {
        country.setClickAction(() => {
          country.addArmies(1)
          activePlayer.removeAvailableArmies(1)
          endPlayersTurn()
        }
        )
      }
    }
  }




  val setupEvent = LogicEvent(List(() => !_setupComplete), List(setup))
  lm.addEvent(setupEvent)
  val isActivePlayerHuman = activePlayer match {
    case x: HumanPlayer => true
    case x: ComputerPlayer => false
  }
  val onClickPlaceArmyEvent = LogicEvent(List(() => isActivePlayerHuman), List(setNonOwnedCountriesOnClickToPlaceArmy))
  lm.addEvent(onClickPlaceArmyEvent)
  val ownedCountryPlaceArmyEvent = LogicEvent(List(() => areAllCountriesOwned), List(setOwnedCountriesOnClickToPlaceArmy))
  lm.addEvent(ownedCountryPlaceArmyEvent)


  val compPlayerTurnTrans = ForwardTransition(List(() => !isActivePlayerHuman), ()=>CompInitPlaceAIState(activePlayer, endPlayersTurn, countries))
  val returnTrans = ReturnTransition(List(() => players.forall(_.availableArmies == 0), () => _setupComplete))
  override val transitions: List[Transition] = List(returnTrans, compPlayerTurnTrans)
}

case class CompInitPlaceAIState(player: Player, endTurn: EndTurnFunction, countries: Map[String, Country]) extends State {

  val areAllCountriesOwned = countries.forall(_._2.owner.nonEmpty)


  def placeArmyOnNonOwnedCountry() = {
    val nonOwnedCountry = countries.find(_._2.owner.isEmpty).get
    nonOwnedCountry._2.addArmies(1)
    nonOwnedCountry._2.setOwner(player)
    player.removeAvailableArmies(1)
  }

  def placeArmyOnOwnedCountry() = {
    val ownedCountry = countries.find(_._2.owner.contains(player)).get
    ownedCountry._2.addArmies(1)
    ownedCountry._2.setOwner(player)
    player.removeAvailableArmies(1)
  }

  def activateReturnState() = _returnState = true


  val nonOwnedCountryPlacementEvent = LogicEvent(List(()=> !areAllCountriesOwned), List(placeArmyOnNonOwnedCountry _, endTurn, activateReturnState _))
  lm.addEvent(nonOwnedCountryPlacementEvent)
  val ownedCountryPlacementEvent = LogicEvent(List(() => areAllCountriesOwned), List(placeArmyOnOwnedCountry _, endTurn, activateReturnState _))
  lm.addEvent(ownedCountryPlacementEvent)


  override val transitions: List[Transition] = Nil
}

