package Service

import TypeAlias.Effect

import scalaz.Scalaz._

/**
  * Created by Harambe on 7/20/2017.
  */
object Effects {

  def begin(wm: WorldMap): Effect[WorldMap] = {

    val effect = init[StateStamp]
    val b = wm.setPhase(InitialPlacement)
    val c = InitPlacePhase.allocateInitArmies(b)
    val d = c.setActivePlayer(1)

    effect.flatMap(_ => state(d))
  }

  def countryClickedDuringInitPlace(wm: WorldMap, countryClicked: Country): Effect[WorldMap] = {
    val effect = init[StateStamp]
    if (InitPlacePhase.isValidInitPlaceArmyPlacement(wm, wm.getActivePlayer.get, countryClicked)) {
      val wm2 = wm.placeArmies(countryClicked, wm.getActivePlayer.get, 1)
      effect.flatMap(_ => state(InitPlacePhase.nextTurn(wm2)))
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

  def countryClickedDuringReinforcementPhase(wm:WorldMap, country:Country): Effect[WorldMap] = {
    val effect = init[StateStamp]

    if(ReinforcementPhase.isValidSource(wm, country)){
      effect.flatMap(_ => state(ReinforcementPhase.setReinformentSource(wm,country)))
    }
    else if (ReinforcementPhase.isValidTarget(wm,country)){
     effect.flatMap(_ => state(ReinforcementPhase.setReinformentTarget(wm, country)))
    } else if (wm.phase.asInstanceOf[Reinforcement].source.map(_.name).contains(country.name)) {
      effect.flatMap(_ => state(wm.setPhase(Reinforcement(None, None))))
    } else effect.flatMap(_ => state(wm))


  }

  def getCountryClickedEffect(worldMap: WorldMap, country:Country): Effect[WorldMap] = {
    worldMap.phase match {
      case InitialPlacement =>
        countryClickedDuringInitPlace(worldMap, country)
      case TurnPlacement =>
        countryClickedDuringTurnPlace(worldMap, country)
      case Attacking(s) =>
        countryClickedDuringAttackingPhase(worldMap, country)
      case Reinforcement(s,t)=>
        countryClickedDuringReinforcementPhase(worldMap, country)
      case _ =>
        val effect = init[StateStamp]
        effect.flatMap(_ => state(worldMap))


    }
  }


  def executeBattle(wm:WorldMap, battleConf: ConfirmBattle, rGen:RandomFactory = new RandomFactory): Effect[WorldMap] = {
    import BattlePhase._
    val effect = init[StateStamp]
    val defArmies = oneOrTwoDefensiveRolls(battleConf.target)
    val battleResults = BattleResult(rGen = rGen).attack(battleConf.offenseArmies, defArmies)
    val wm2 = recordResults(wm, battleResults)
    val wm3 = removeDeadArmies(wm2)
    val wm4 = handleIfCountryConquered(wm3)

    effect.flatMap(_ => state(wm4))
  }

  def executeTransfer(wm:WorldMap, confTrans:ConfirmTransfer): Effect[WorldMap] = {
    wm.phase match {
      case Battle(_,_,_,_) =>
        executeBattleTransfer(wm, confTrans)
      case Reinforcement(_,_) =>
        executeReinforcementTransfer(wm, confTrans)
    }
  }

  def executeReinforcementTransfer(wm:WorldMap, confTrans:ConfirmTransfer): Effect[WorldMap] = {
    val effect = init[StateStamp]
    val phase = wm.phase.asInstanceOf[Reinforcement]
    val wm2 = wm.transferArmies(phase.source.get, phase.target.get, confTrans.amount)
    val wm3 = ReinforcementPhase.nextTurn(wm2)

    effect.flatMap(_ => state(wm3))
  }


  def executeBattleTransfer(wm:WorldMap, confTrans:ConfirmTransfer): Effect[WorldMap] = {
    val effect = init[StateStamp]
    val battle = wm.phase.asInstanceOf[Battle]
    val wm2 = wm.transferArmies(battle.source, battle.target, confTrans.amount)
    val wm3 = wm2.setPhase(Attacking(None))

    effect.flatMap(_ => state(wm3))
  }

  def retreatFromBattle(wm:WorldMap): Effect[WorldMap] = {
    val effect = init[StateStamp]
    val wm2 = wm.setPhase(Attacking(None))

    effect.flatMap(_ => state(wm2))
  }

  def endAttackPhase(wm:WorldMap): Effect[WorldMap] = {
    val effect = init[StateStamp]
    val wm2 = wm.setPhase(Reinforcement(None, None))

    effect.flatMap(_ => state(wm2))
  }


}
