import WorldMap.WorldMapState
import TypeAlias._

import scalaz.Scalaz._
import scalaz.{Monad, State}


case class StateStamp(id: Int)

trait Validation
case object Success extends Validation
case object Failure extends Validation


object Effects {

  def begin(wm: WorldMap): Effect[WorldMap] = {

    val effect = init[StateStamp]
    val b = wm.setPhase(InitialPlacement)
    val c = InitPlacePhase.allocateInitArmies(b)
    val d = c.setActivePlayer(1)
    val e = WorldMap.beginActivePlayerTurn(d)

    effect.flatMap(_ => state(e))
  }

  def countryClickedDuringInitPlace(wm: WorldMap, countryClicked: Country): Effect[WorldMap] = {
    val effect = init[StateStamp]
    if (InitPlacePhase.isValidInitPlaceArmyPlacement(wm, wm.getActivePlayer.get, countryClicked)) {
      val wm2 = wm.placeArmies(countryClicked, wm.getActivePlayer.get, 1)
      effect.flatMap(_ => state(InitPlacePhase.nextInitPlaceTurn(wm2)))
    }
    else {
      effect.flatMap(_ => state(wm))
    }
  }

  def countryClickedDuringTurnPlace(wm:WorldMap, countryClicked:Country):Effect[WorldMap] = {
    val effect = init[StateStamp]
    val wm2 = TurnPlacePhase.attemptToPlaceArmy(wm, countryClicked)

    if(TurnPlacePhase.checkEndOfTurnPlacement(wm2)){
      effect.flatMap(_ => state(TurnPlacePhase.endTurnPlacementPhase(wm2)))
    }
    else effect.flatMap(_ => state(wm2))
  }

  def countryClickedDuringAttackingPhase(wm:WorldMap, countryClicked:Country):Effect[WorldMap] = {
    val effect = init[StateStamp]

    wm.phase match {
      case Attacking(None) =>
        effect.flatMap(_ => state(AttackingPhase.selectSource(wm, countryClicked)))

      case Attacking(s:Some[Country]) =>
        if(s.get.name == countryClicked.name)
          effect.flatMap(_ => state(AttackingPhase.deselectSource(wm)))
        else
          effect.flatMap(_ => state(AttackingPhase.beginBattle(wm, countryClicked)))
    }


  }

  def getCountryClickedEffect(worldMap: WorldMap, country:Country): Effect[WorldMap] = {
    worldMap.phase match {
      case InitialPlacement =>
        countryClickedDuringInitPlace(worldMap, country)
      case TurnPlacement =>
        countryClickedDuringTurnPlace(worldMap, country)
      case Attacking(s) =>
        countryClickedDuringAttackingPhase(worldMap, country)


    }
  }
}

class SideEffectManager(setWM: (WorldMap) => Unit) {

  private var mutations = 0
  private def recordMutation() = mutations += 1

  def performEffect(effect:Effect[WorldMap]) : Unit = {
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
