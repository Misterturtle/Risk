package Service

import Service.TypeAlias.{Effect, Event}
import scalaz.State

/**
  * Created by Harambe on 7/17/2017.
  */
object TypeAlias {
  type Effect[A] = State[StateStamp, A]
  type Event[A] = State[WorldMap, A]
}

object Effect {
  def apply(f: StateStamp => (StateStamp, WorldMap)): Effect[WorldMap] = State[StateStamp, WorldMap] {
    ss => f(ss)
  }
}

object Event {
  def apply[A](f: WorldMap => (WorldMap, A)): Event[A] = State[WorldMap, A] {
    worldMap => f(worldMap)
  }
}
