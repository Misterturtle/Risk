import _root_.WorldMap._

import scalaz.Scalaz._

/**
  * Created by Harambe on 7/13/2017.
  */
object StateTransitions {


  def begin(stateStamp: StateStamp, wm: WorldMap): Unit = {
    val stateTrans = for {
      a <- init[WorldMap]
      b <- state(a.copy(stateStamp = SideEffectValidator.stamp))
      c <- state(WorldMap.setPhase(b, InitialPlacement))
      d <- state(WorldMap.allocateInitArmies(c))
      e <- state(WorldMap.setActivePlayer(d, Some(d.players.head)))
      f <- state(WorldMap.beginActivePlayerTurn(e))
    } yield f

    val wm = stateTrans.eval(Main.getCurrentWorldMap)
    val validation = SideEffectValidator.validateStateStamp(wm.stateStamp)

    validation match{
      case Sucess =>
        Main.mutateWorldMap(wm)
      case Failure =>
        println("WorldMap from Begin Method failed to validate")
    }
  }

  def initPlaceClickAction(player:HumanPlayer)(country: Country): Unit = {
    val stateTrans = for {
        a <- init[WorldMap]
        b <- state(a.copy(stateStamp = SideEffectValidator.stamp))
        newPlayers = a.players.map(p => if(p == player) player.removeArmies(1) else p)
        newCountries = a.countries.map(c => if(c == country) c.addArmies(1).setOwner(player) else c)
        c <- state(b.copy(countries = newCountries, players = newPlayers))
      } yield c

    val wm = stateTrans.eval(Main.getCurrentWorldMap)
    val validation = SideEffectValidator.validateStateStamp(wm.stateStamp)

    validation match{
      case Sucess =>
        Main.mutateWorldMap(wm)
      case Failure =>
        println("WorldMap from Begin Method failed to validate")
    }
  }



}
