package Service

import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FreeSpec, Matchers}

class SideEffectManagerTest extends FreeSpec with Matchers with MockitoSugar {

  "receive performs the service effect passed in" in {
    val mockSideEffectManager = mock[SideEffectManager]
    val mockEffect = mock[Action[WorldMap]]
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

  def createDelayEvent(): Action[WorldMap] = Action { worldMap: WorldMap =>
    println("Inside")
    Thread.sleep(2000)

    worldMap
  }

  def createQuickEvent(): Action[WorldMap] = Action {}

}
