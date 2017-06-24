import javafx.event.EventHandler
import javafx.scene.input.MouseEvent

import org.scalatest.{FreeSpec, Matchers}

import scala.collection.mutable.ListBuffer
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.layout.Pane

/**
  * Created by Harambe on 6/18/2017.
  */
class InitialPlacingArmiesStateTests extends FreeSpec with Matchers{

  "If a country is not owned during InitPlaceState and the active player is human, it should" - {

    "Have an onClickAction to place an army from a player" in {
      val mockCountryFactory = new CountryFactory(){
        override def newCountry(name: String, origPoints: List[(Double, Double)]) : Country = {
          new Country(name, origPoints){
            override def setClickAction(action:()=>Unit): Unit ={
              _clickAction = action
            }
          }
        }
      }
      val mockPlayer = new Player(true)
      mockPlayer.addAvailableArmies(5)

      val country1 = mockCountryFactory.newCountry("mock1", List())
      val country2 = mockCountryFactory.newCountry("mock2", List())
      val mockCountries = Map[String,Country]("mock1" -> country1, "mock2" -> country2)
      val initialPlacementState = InitPlaceState(List(mockPlayer), mockCountries)

      initialPlacementState.update()
      country1.getClickAction()()

      country1.armies shouldBe 1
      country1.owner shouldBe Some(mockPlayer)
      mockPlayer.availableArmies shouldBe 4
    }


    "End the turn after an army is placed" in {
      val mockCountryFactory = new CountryFactory(){
        override def newCountry(name: String, origPoints: List[(Double, Double)]) : Country = {
          new Country(name, origPoints){
            override def setClickAction(action:()=>Unit): Unit ={
              _clickAction = action
            }
          }
        }
      }
      val mockPlayer = new Player(true)
      mockPlayer.addAvailableArmies(5)
      val compPlayer = new Player(false)

      val country1 = mockCountryFactory.newCountry("mock1", List())
      val mockCountries = Map[String,Country]("mock1" -> country1)
      val initialPlacementState = InitPlaceState(List(mockPlayer, compPlayer), mockCountries)

      initialPlacementState.update()
      country1.getClickAction()()

      initialPlacementState.activePlayer shouldBe compPlayer
    }

    "Not place a 2nd army if clicked multiple times" in {
      val mockCountryFactory = new CountryFactory(){
        override def newCountry(name: String, origPoints: List[(Double, Double)]) : Country = {
          new Country(name, origPoints){
            override def setClickAction(action:()=>Unit): Unit ={
              _clickAction = action
            }
          }
        }
      }
      val mockPlayer = new Player(true)
      mockPlayer.addAvailableArmies(5)

      val country1 = mockCountryFactory.newCountry("mock1", List())
      val country2 = mockCountryFactory.newCountry("mock2", List())
      val mockCountries = Map[String,Country]("mock1" -> country1, "mock2" -> country2)
      val initialPlacementState = InitPlaceState(List(mockPlayer), mockCountries)

      initialPlacementState.update()
      country1.getClickAction()()
      country1.getClickAction()()
      country1.getClickAction()()
      country1.getClickAction()()

      country1.armies shouldBe 1
      country1.owner shouldBe Some(mockPlayer)
      mockPlayer.availableArmies shouldBe 4
    }
  }




  "If the active player has no available armies left, the state should activate the return transition" in {
    val mockPlayer = new Player(true)
    val mockCountries = Map[String,Country]()
    val initialPlacementState = InitPlaceState(List(mockPlayer), mockCountries)

    initialPlacementState.update()

    initialPlacementState.returnState shouldBe true
  }











  def initGraphics(root: Pane): Unit ={
    new PrimaryStage{
      scene = new Scene(root, 1000, 800)
    }
  }


}
