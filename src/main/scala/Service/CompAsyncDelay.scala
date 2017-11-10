package Service

import scala.concurrent.Future



class CompAsyncDelay() {

  val beginTurnDelay = 4000
  val placementDelay = 1000
  val attackSourceDelay = 1000
  val attackTargetDelay = 1000

  def beginTurnDelay(wm:WorldMap): Future[CompInput] = {
    Future{
      Thread.sleep(beginTurnDelay)
      PlacementDelay
    }
  }

  def placementDelay(wm:WorldMap): Future[CompInput] = Future{
    Thread.sleep(placementDelay)
    PlacementDelay
  }

  def attackSourceDelay(wm:WorldMap) : Future[CompInput] = Future{
    Thread.sleep(attackSourceDelay)
    AttackSourceDelay
  }

  def attackTargetDelay(wm:WorldMap) : Future[CompInput] = Future{
    Thread.sleep(attackTargetDelay)
    AttackTargetDelay
  }



}
