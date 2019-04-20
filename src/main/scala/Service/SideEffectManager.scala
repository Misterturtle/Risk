package Service

import TypeAlias.Effect

case class StateStamp(id: Int)

trait Validation

case object Failure extends Validation

case object Success extends Validation

object SideEffectManager {
  private var _sideEffectManager: Option[SideEffectManager] = None

  def setNewSingleton(sideEffectManager: SideEffectManager) = _sideEffectManager = Some(sideEffectManager)

  def receive(effect: Effect[WorldMap]): Validation ={
    _sideEffectManager.map(manager => manager.performServiceEffect(effect)).getOrElse(Failure)
  }
}

class SideEffectManager(worldMapController: WorldMapController, worldMapUIController: WorldMapUIController) {

  private var mutations = 0

  private def recordMutation() = mutations += 1

  def performServiceEffect(effect: Effect[WorldMap]): Validation = {
    val stampWithWM = effect.run(stamp)
    validateStateStamp(stampWithWM._1) match {
      case Success =>
        recordMutation()
        worldMapController.mutateWorldMapUnsafe(stampWithWM._2)
        worldMapUIController.updateWorldMap(stampWithWM._2)
        Success

      case Failure =>
        println("Effect failed to validate state stamp")
        Failure
    }
  }


  private def stamp: StateStamp = {
    StateStamp(mutations)
  }

  private def validateStateStamp(stateStamp: StateStamp): Validation = {
    if (stateStamp.id == mutations)
      Success
    else Failure
  }
}
