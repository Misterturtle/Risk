import org.scalatest.{FreeSpec, Matchers}

/**
  * Created by Harambe on 6/18/2017.
  */
class WorldMapStateTransTests extends FreeSpec with Matchers {

  "The world map should transition to the initial placement state if the initial placement flag is false" in {

    val worldMap = new WorldMap(new CountryFactory)
    worldMap.baseState.update()

    worldMap.baseState.forwardState shouldBe Some(InitPlaceState())
  }

  "The world map should NOT transition to the initial placement state if the initial placement complete flag is true" in {

    val worldMap = new WorldMap(new CountryFactory){
      _initialPlacementComplete = true
    }
    worldMap.baseState.update()

    worldMap.baseState.forwardState shouldBe None
  }








}
