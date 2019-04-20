package Service

import org.scalatest.{FreeSpec, Matchers}
import org.scalatest.mockito.MockitoSugar
import TypeAlias.Effect
import org.mockito.Mockito._
import scalaz.Scalaz.init
import scalaz.Scalaz.state
import scalaz.State
import Service.Success

class SideEffectManagerTest extends FreeSpec with Matchers with MockitoSugar {

  "receive performs the service effect passed in" in {
    val mockSideEffectManager = mock[SideEffectManager]
    val mockEffect = mock[Effect[WorldMap]]
    SideEffectManager.setNewSingleton(mockSideEffectManager)

    SideEffectManager.receive(mockEffect)

    verify(mockSideEffectManager).performServiceEffect(mockEffect)
  }

  "Only one action can be performed at a time" in {
//    val mockSideEffectManager = mock[SideEffectManager]
    val subject = new SideEffectManager(new WorldMapController(), mock[WorldMapUIController])

    val timeConsumingEffect = createDelayEvent()
    val quickEffect = createQuickEvent()
    SideEffectManager.setNewSingleton(subject)

    val firstResult = SideEffectManager.receive(timeConsumingEffect)
    val secondResult = SideEffectManager.receive(quickEffect)

    firstResult shouldBe Success
    secondResult shouldBe Success
  }

  def createDelayEvent(): Effect[WorldMap] ={
    val a = init[StateStamp]
    val b = a.map(ss => {
      println("Inside")
      Thread.sleep(2000)
      ss
    })

    b.flatMap(_ => state(mock[WorldMap]))
  }

  def createQuickEvent(): Effect[WorldMap] = {
    val a = init[StateStamp]

    a.flatMap(_ => state(mock[WorldMap]))
  }

}
