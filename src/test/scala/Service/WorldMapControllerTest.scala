package Service

import org.scalatest.mockito.MockitoSugar
import org.mockito.Mockito._
import org.scalatest.{FreeSpec, FunSuite, Matchers}
import org.mockito.Matchers._


class WorldMapControllerTest extends FreeSpec with Matchers with MockitoSugar{

  private val mockSideEffectManager = mock[SideEffectManager]


  "begin" - {

    "performs the begin effect" in {
      SideEffectManager.setNewSingleton(mockSideEffectManager)
      val subject = new WorldMapController()

      subject.begin()

      verify(mockSideEffectManager).performServiceEffect(any(classOf[Action[WorldMap]]))
    }
  }

}
