package common

import Service.{Action, Country, Phase, Player, WorldMap}


object Common {

  implicit class Pipe[A](val a: A) extends AnyVal {
    def >>[B](f: A => B): B = f(a)
    def >>(action: Action[A]) : A = action.run(a)
  }

  implicit class CombineAction[A](val firstAction: Action[A]) extends AnyVal {
    def >>(secondAction: Action[A]): Action[A] = Action { a: A =>
      secondAction.run(firstAction.run(a))
    }
  }


  implicit def lift[A](a:A) : Action[A] = Action { _ => a}


  implicit class PlayerActions(val playerAction: Action[List[Player]]) extends AnyVal {
    def >>>(worldMapAction: Action[WorldMap]): Action[WorldMap] = Action { worldMap: WorldMap =>
      val worldMapResult = worldMap.copy(players = playerAction.run(worldMap.players))
      worldMapAction.run(worldMapResult)
    }
  }

  implicit def liftPlayerAction(playerAction: Action[List[Player]]): Action[WorldMap] = Action { worldMap: WorldMap =>
    worldMap.copy(players = playerAction.run(worldMap.players))
  }


  implicit class PhaseActions(val phaseAction: Action[Phase]) extends AnyVal {
    def >>>(worldMapAction: Action[WorldMap]): Action[WorldMap] = Action { worldMap: WorldMap =>
      val worldMapResult = worldMap.copy(phase = phaseAction.run(worldMap.phase))
      worldMapAction.run(worldMapResult)
    }
  }

  implicit def liftPhaseAction(phaseAction: Action[Phase]): Action[WorldMap] = Action { worldMap: WorldMap =>
    worldMap.copy(phase = phaseAction.run(worldMap.phase))
  }


  implicit class CountryActions(val countryAction: Action[List[Country]]) extends AnyVal {
    def >>>(worldMapAction: Action[WorldMap]): Action[WorldMap] = Action { worldMap: WorldMap =>
      val worldMapResult = worldMap.copy(countries = countryAction.run(worldMap.countries))
      worldMapAction.run(worldMapResult)
    }
  }

  implicit def liftCountryAction(countryAction: Action[List[Country]]): Action[WorldMap] = Action { worldMap: WorldMap =>
    worldMap.copy(countries = countryAction.run(worldMap.countries))
  }



  //experimental
//  implicit def autoRunAction[A](action: Action[A]): A => A = { a: A => action.run(a) }

}
