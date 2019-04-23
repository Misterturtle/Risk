package Service

import Service.TypeAlias.{Effect, Effect__}
import scalaz.State

/**
  * Created by Harambe on 7/17/2017.
  */
object TypeAlias {
  type Effect[A] = State[StateStamp[A], A]
  type Effect__[A] = State[IdentityMonad[A], A]
}

object Effect__ {
  def apply[A](f: => A => A): Effect__[A] = State[IdentityMonad[A], A] {id =>
    (id, f(id.originalData))
  }

  def apply[A](f: A => Effect__[A]): Effect__[A] = State[IdentityMonad[A], A] { id =>
    f(id.originalData).apply(id)
  }
}

object Effect {
  def apply[A](f: => A => A): Effect[A] = State[StateStamp[A], A] {ss =>
    (ss, f(ss.originalData))
  }

  def apply[A](f: A => Effect[A]): Effect[A] = State[StateStamp[A], A] { ss =>
    f(ss.originalData).apply(ss)
  }
}

