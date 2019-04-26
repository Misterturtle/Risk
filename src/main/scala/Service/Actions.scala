package Service

import common.Common._

object Actions {

  def begin(): Action[WorldMap] = FlatAction { worldMap: WorldMap =>
    InitPlacePhase.setInitialPhase >>>
      InitPlacePhase.allocateInitArmies >>
      WorldMap.setActivePlayer(1)
  }

  def countryClickedDuringInitPhase(countryClicked: Country): Action[WorldMap] = FlatAction { worldMap: WorldMap =>

    if (InitPlacePhase.isValidInitPlaceArmyPlacement(worldMap, worldMap.getActivePlayer.get, countryClicked)) {
      val wm2:WorldMap = worldMap >>
        WorldMap.placeArmies(countryClicked.name, worldMap.getActivePlayer.get, 1)

      Action { _ => wm2 >> InitPlacePhase.nextTurn}
    }
    else {
      Action {}
    }
  }

  def countryClickedDuringTurnPhase(countryClicked: Country): Action[WorldMap] = FlatAction { worldMap: WorldMap =>

    val activePlayerNumber = worldMap.getActivePlayer.get.playerNumber

    if(countryClicked.isOwnedBy(activePlayerNumber)){
      val newCountries = worldMap.countries.map{country =>
        if(country.name == countryClicked.name){
          countryClicked.copy(armies = countryClicked.armies + 1)
        } else {
          country
        }
      }

      val wm5:WorldMap = Player.removeArmies(activePlayerNumber, 1).run(worldMap)
        .copy(countries = newCountries)

      if (TurnPlacePhase.checkEndOfTurnPlacement(wm5)) {
        wm5 >> TurnPlacePhase.endTurnPlacementPhase()
      } else {
        Action { _ => wm5 }
      }
    } else {
      Action {}
    }


  }

  def countryClickedDuringAttackingPhase(countryClicked: Country): Action[WorldMap] = FlatAction { worldMap: WorldMap =>
    worldMap.phase match {
      case Attacking(None, _) =>
        AttackingPhase.selectSource(countryClicked)

      case Attacking(s: Some[Country], _) =>
        if (s.get.name == countryClicked.name)
          AttackingPhase.deselectSource()
        else
          AttackingPhase.beginBattle(countryClicked)
    }
  }

  def countryClickedDuringReinforcementPhase(country: Country): Action[WorldMap] = Action { worldMap: WorldMap =>
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

  def countryClicked(country: Country): Action[WorldMap] = FlatAction { worldMap: WorldMap =>
    worldMap.phase match {
      case InitialPlacement =>
        countryClickedDuringInitPhase(country)
      case TurnPlacement =>
        countryClickedDuringTurnPhase(country)
      case Attacking(s, _) =>
        countryClickedDuringAttackingPhase(country)
      case Reinforcement(s, t) =>
        countryClickedDuringReinforcementPhase(country)
      case _ =>
        Action {}
    }
  }

  def executeBattle(battleConf: ConfirmBattle, rGen: RandomFactory = new RandomFactory): Action[WorldMap] = Action { worldMap: WorldMap =>
    import BattlePhase._
    val defArmies = oneOrTwoDefensiveRolls(battleConf.target)
    val battleResults = BattleResult(rGen = rGen).attack(battleConf.offenseArmies, defArmies)
    val wm2 = recordResults(worldMap, battleResults)
    val wm3 = removeDeadArmies(wm2)
    val wm4 = checkForBattleEnd(wm3)

    wm4
  }

  def executeTransfer(confTrans: ConfirmTransfer): Action[WorldMap] = FlatAction { worldMap: WorldMap =>
    worldMap.phase match {
      case Battle(_, _, _, _) =>
        executeBattleTransfer(confTrans)
      case Reinforcement(_, _) =>
        executeReinforcementTransfer(confTrans)
    }
  }

  def executeReinforcementTransfer(confTrans: ConfirmTransfer): Action[WorldMap] = Action { worldMap: WorldMap =>
    val phase = worldMap.phase.asInstanceOf[Reinforcement]
    val wm2 = worldMap.transferArmies(phase.source.get, phase.target.get, confTrans.amount)
    val wm3 = ReinforcementPhase.nextTurn(wm2)

    wm3
  }


  def executeBattleTransfer(confTrans: ConfirmTransfer): Action[WorldMap] = Action { worldMap: WorldMap =>
    val battle = worldMap.phase.asInstanceOf[Battle]
    val wm2 = worldMap.transferArmies(battle.source, battle.target, confTrans.amount)
    val wm3 = wm2.setPhase(Attacking(None, None))

    wm3
  }

  def retreatFromBattle(): Action[WorldMap] = Action { worldMap: WorldMap =>
    worldMap.setPhase(Attacking(None, None))
  }

  def endAttackPhase(): Action[WorldMap] = Action { worldMap: WorldMap =>
    worldMap.setPhase(Reinforcement(None, None))
  }
}
