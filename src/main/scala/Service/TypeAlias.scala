package Service

import scalaz._

/**
  * Created by Harambe on 7/17/2017.
  */
object TypeAlias {
  type Effect[A] = State[StateStamp, A]
}
