/**
  * Created by Harambe on 6/16/2017.
  */
trait Transition{
  val conditions: List[() => Boolean]
}
case class ForwardTransition(conditions: List[() => Boolean], forwardTransState: State) extends Transition
case class ReturnTransition(conditions: List[() => Boolean]) extends Transition

abstract class State() {

  protected var _forwardState: Option[State] = None
  protected var _returnState:Boolean = false
  def forwardState: Option[State] = _forwardState

  val transitions: List[Transition]

  def returnState:Boolean = _returnState

  def update(): Unit = {
    checkReactivation()
    _forwardState match {
      case Some(state) =>
        state.update()

      case None =>
        checkTransitions()
    }
  }

  private def checkReactivation(): Unit ={
    if(_forwardState.exists(_.returnState))
      _forwardState = None
  }

  private def checkTransitions(): Unit ={
    transitions.find(_.conditions.forall(_())) match {
      case Some(forwardTrans:ForwardTransition) =>
        _forwardState = Some(forwardTrans.forwardTransState)

      case Some(returnTrans:ReturnTransition) =>
        _returnState = true

      case None =>
    }
  }
}


class TestState(val transitions:List[Transition]) extends State()

case class WorldMapState(initPlaceComp: () => Boolean) extends State(){


  val transToInitPlace = ForwardTransition(List(() => !initPlaceComp()), InitPlaceState())
  val transitions = List[Transition](transToInitPlace)
}

case class InitPlaceState() extends State{
  override val transitions: List[Transition] = Nil
}