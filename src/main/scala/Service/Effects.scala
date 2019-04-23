package Service

import TypeAlias.{Effect, Event}

import common.Common._

object Effects {

  def begin(): Effect[WorldMap] = Effect { worldMap:WorldMap =>

    val c = (
      worldMap.setPhase(InitialPlacement)
        >> InitPlacePhase.allocateInitArmies
      )
    val newWM = c.setActivePlayer(1)

    newWM
  }

  def countryClickedDuringInitPlace(countryClicked: Country): Effect[WorldMap] = Effect { worldMap:WorldMap =>
    if (InitPlacePhase.isValidInitPlaceArmyPlacement(worldMap, worldMap.getActivePlayer.get, countryClicked)) {
      worldMap.placeArmies(countryClicked, worldMap.getActivePlayer.get, 1)
    }
    else {
      worldMap
    }
  }

  def countryClickedDuringTurnPlace(countryClicked: Country): Effect[WorldMap] = Effect { worldMap:WorldMap =>

    val newWorldMap = worldMap >> TurnPlacePhase.attemptToPlaceArmy(countryClicked)

    if (TurnPlacePhase.checkEndOfTurnPlacement(newWorldMap)) {
      TurnPlacePhase.endTurnPlacementPhase(newWorldMap)
    }
    else newWorldMap
  }

  def countryClickedDuringAttackingPhase(countryClicked: Country): Effect[WorldMap] = Effect { worldMap:WorldMap =>
    worldMap.phase match {
      case Attacking(None, _) =>
        AttackingPhase.selectSource(worldMap, countryClicked)

      case Attacking(s: Some[Country], _) =>
        if (s.get.name == countryClicked.name)
          AttackingPhase.deselectSource(worldMap)
        else
          AttackingPhase.beginBattle(worldMap, countryClicked)
    }
  }

  def countryClickedDuringReinforcementPhase(country: Country): Effect[WorldMap] = Effect { worldMap:WorldMap =>
    if (ReinforcementPhase.isValidSource(worldMap, country)) {
      ReinforcementPhase.setReinformentSource(worldMap, country)
    }
    else if (ReinforcementPhase.isValidTarget(worldMap, country)) {
      ReinforcementPhase.setReinformentTarget(worldMap, country)
    } else if (worldMap.phase.asInstanceOf[Reinforcement].source.map(_.name).contains(country.name)) {
      worldMap.setPhase(Reinforcement(None, None))
    } else {
      worldMap
    }
  }

  def countryClicked(country: Country): Effect[WorldMap] = Effect { worldMap:WorldMap =>
    worldMap.phase match {
      case InitialPlacement =>
        countryClickedDuringInitPlace(country)
      case TurnPlacement =>
        countryClickedDuringTurnPlace(country)
      case Attacking(s, _) =>
        countryClickedDuringAttackingPhase(country)
      case Reinforcement(s, t) =>
        countryClickedDuringReinforcementPhase(country)
      case _ =>
        Effect { worldMap:WorldMap => worldMap}
    }
  }

  def executeBattle(battleConf: ConfirmBattle, rGen: RandomFactory = new RandomFactory): Effect[WorldMap] = Effect { worldMap: WorldMap =>
    import BattlePhase._
    val defArmies = oneOrTwoDefensiveRolls(battleConf.target)
    val battleResults = BattleResult(rGen = rGen).attack(battleConf.offenseArmies, defArmies)
    val wm2 = recordResults(worldMap, battleResults)
    val wm3 = removeDeadArmies(wm2)
    val wm4 = checkForBattleEnd(wm3)

    wm4
  }

  def executeTransfer(confTrans: ConfirmTransfer): Effect[WorldMap] = Effect { worldMap:WorldMap =>
    worldMap.phase match {
      case Battle(_, _, _, _) =>
        executeBattleTransfer(confTrans)
      case Reinforcement(_, _) =>
        executeReinforcementTransfer(confTrans)
    }
  }

  def executeReinforcementTransfer(confTrans: ConfirmTransfer): Effect[WorldMap] = Effect { worldMap:WorldMap =>
    val phase = worldMap.phase.asInstanceOf[Reinforcement]
    val wm2 = worldMap.transferArmies(phase.source.get, phase.target.get, confTrans.amount)
    val wm3 = ReinforcementPhase.nextTurn(wm2)

    wm3
  }


  def executeBattleTransfer(confTrans: ConfirmTransfer): Effect[WorldMap] = Effect { worldMap:WorldMap =>
    val battle = worldMap.phase.asInstanceOf[Battle]
    val wm2 = worldMap.transferArmies(battle.source, battle.target, confTrans.amount)
    val wm3 = wm2.setPhase(Attacking(None, None))

    wm3
  }

  def retreatFromBattle(): Effect[WorldMap] = Effect { worldMap:WorldMap =>
    worldMap.setPhase(Attacking(None, None))
  }

  def endAttackPhase(): Effect[WorldMap] = Effect { worldMap:WorldMap =>
    worldMap.setPhase(Reinforcement(None, None))
  }
}
