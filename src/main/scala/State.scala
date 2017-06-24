/**
  * Created by Harambe on 6/16/2017.
  */
trait Transition {
  val conditions: List[() => Boolean]
}

case class ForwardTransition(conditions: List[() => Boolean], forwardTransState: State) extends Transition

case class ReturnTransition(conditions: List[() => Boolean]) extends Transition

abstract class State(protected val lm: LogicMachine = new LogicMachine()) {

  protected var _forwardState: Option[State] = None
  protected var _returnState: Boolean = false

  def forwardState: Option[State] = _forwardState

  val transitions: List[Transition]

  def returnState: Boolean = _returnState

  def update(): Unit = {
    checkReactivation()
    _forwardState match {
      case Some(state) =>
        state.update()

      case None =>
        checkTransitions()
    }
  }

  private def checkReactivation(): Unit = {
    if (_forwardState.exists(_.returnState))
      _forwardState = None
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


  val transToInitPlace = ForwardTransition(List(() => !initPlaceComp()), InitPlaceState(players, countries))
  val transitions = List[Transition](transToInitPlace)
}

case class InitPlaceState(players: List[Player], countries: Map[String, Country]) extends State {

  var activePlayer = players.head

  def endPlayersTurn(): Unit = {
    countries.foreach(_._2.setClickAction(() => {}))

    if (players.isDefinedAt(activePlayer.playerNumber + 1))
      activePlayer = players(activePlayer.playerNumber + 1)
    else
      activePlayer = players.head

  }

  def isCountryOwned(country: Country): Boolean = country.owner.nonEmpty

  def setCountriesOnClickToPlaceArmy() = {
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

  val onClickPlaceArmyEvent = LogicEvent(List(() => activePlayer.isHuman), List(setCountriesOnClickToPlaceArmy))
  lm.addEvent(onClickPlaceArmyEvent)






  val returnTrans = ReturnTransition(List(()=>players.forall(_.availableArmies == 0)))
  override val transitions: List[Transition] = List(returnTrans)
}