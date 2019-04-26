package Service

case class StateStamp[A](id: Int, originalData: A)

trait Validation

case object Failure extends Validation

case object Success extends Validation

object SideEffectManager {
  private var _sideEffectManager: Option[SideEffectManager] = None

  def setNewSingleton(sideEffectManager: SideEffectManager) = _sideEffectManager = Some(sideEffectManager)

  def receive(action: Action[WorldMap]): Validation = {
    _sideEffectManager.map(manager => manager.performServiceEffect(action)).getOrElse(Failure)
  }
}

class SideEffectManager(worldMapController: WorldMapController, worldMapUIController: WorldMapUIController) {

  def performServiceEffect(action: Action[WorldMap]): Validation = {
    val processedWorldMap = action.run(worldMapController.getCurrentWorldMap)
    worldMapController.mutateWorldMapUnsafe(processedWorldMap)
    worldMapUIController.updateWorldMap(processedWorldMap)

    //todo add error handling
    //todo add async handling
    Success
  }

}
