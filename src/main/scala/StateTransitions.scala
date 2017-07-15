import scalaz._
import Scalaz._
import WorldMap._


/**
  * Created by Harambe on 7/13/2017.
  */
object StateTransitions {


  def begin(wm: WorldMap): Unit = {
    val stateTrans = for {
      a <- init[WorldMap]
      b <- state(a.copy(stateStamp = SideEffectValidator.stamp))
      c <- state(WorldMap.setPhase(b, InitialPlacement))
      d <- state(WorldMap.allocateInitArmies(c))
      e <- state(WorldMap.setActivePlayer(d, 1))
      f <- state(WorldMap.beginActivePlayerTurn(e))
    } yield f

    val wm = stateTrans.eval(Main.getCurrentWorldMap)
    val validation = SideEffectValidator.validateStateStamp(wm.stateStamp)

    validation match{
      case Sucess =>
        println("Woohoo")
        Main.mutateWorldMap(wm)
      case Failure =>
        println("WorldMap from Begin Method failed to validate")
    }
  }

  def initPlaceClickAction(player:HumanPlayer)(country: Country): Unit = {
    println("Clicked " + country.name)

    val stateTrans = for {
        a <- init[WorldMap]
        b <- state(a.copy(stateStamp = SideEffectValidator.stamp))
        newPlayers = b.players.map(p => if(p == player) player.removeArmies(1) else p)
        newCountries = b.countries.map(c => if(c.name == country.name) c.addArmies(1).setOwner(player) else c)
        d <- state(b.copy(countries = newCountries, players = newPlayers))
        e <- state(WorldMap.nextTurn(d))
      } yield e

    val wm:WorldMap = stateTrans.eval(Main.getCurrentWorldMap)
    val validation = SideEffectValidator.validateStateStamp(wm.stateStamp)

    validation match{
      case Sucess =>
        Main.mutateWorldMap(wm)
      case Failure =>
        println("WorldMap from Begin Method failed to validate")
    }
  }

  def turnPlacementClickAction(country:Country, player:Player): Unit = {

  }



}
