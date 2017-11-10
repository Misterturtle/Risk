package Service

import TypeAlias.Effect

/**
  * Created by Harambe on 7/20/2017.
  */
case class StateStamp(id: Int)

trait Validation

case object Failure extends Validation

case object Success extends Validation



class SideEffectManager(setWM: (WorldMap) => Unit) {

  private var mutations = 0
  private def recordMutation() = mutations += 1

  def performServiceEffect(effect:Effect[WorldMap]) : Unit = {
    val stampWithWM = effect.run(stamp)
    validateStateStamp(stampWithWM._1) match {
      case Success =>
        recordMutation()
        setWM(stampWithWM._2)

      case Failure =>
        println("Effect failed to validate state stamp")
    }
  }

  private def stamp: StateStamp ={
    StateStamp(mutations)
  }

  private def validateStateStamp(stateStamp: StateStamp): Validation = {
    if(stateStamp.id == mutations)
      Success
    else Failure
  }
}
