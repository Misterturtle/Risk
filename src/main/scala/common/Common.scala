package common

import Service.{Effect, Effect__, IdentityMonad, Player, StateStamp, WorldMap}
import Service.TypeAlias.{Effect, Effect__}
import scalaz.State

object Common{
  implicit class Pipe[A](val a: A) extends AnyVal {
    def >>[B](f: A => B): B = f(a)
  }


  def liftPlayer(player:Player): Effect__[WorldMap] = Effect__ { worldMap:WorldMap =>
    worldMap >>
      WorldMap.updatePlayer(player)
  }

  def flattenPlayer(playerEffect: Effect__[Player]): Effect__[WorldMap] = Effect__ { worldMap:WorldMap =>
    val player = worldMap.players.find(player => player.playerNumber == playerEffect.)
  }

}
