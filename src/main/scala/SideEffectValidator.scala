import _root_.WorldMap.WorldMapState

sealed case class StateStamp(id:Int)

trait Validation
case object Sucess extends Validation
case object Failure extends Validation

trait Stateful{
  val stateStamp:StateStamp
}

object SideEffectValidator {

  private var mutations = 0
  private def recordMutation() = mutations += 1

  def stamp: StateStamp ={
    StateStamp(mutations)
  }

  def validateStateStamp(stateStamp: StateStamp): Validation = {
    if(stateStamp.id == mutations)
      Sucess
    else Failure
  }

  def mutateWorldMap(wmState: WorldMapState[_], stateStamp: StateStamp): Unit ={
    validateStateStamp(stateStamp) match {
      case Sucess =>
        recordMutation()
        Main.mutateWorldMap(wmState.exec(Main.getCurrentWorldMap))
      case Failure =>
    }

  }

}
