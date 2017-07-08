import javafx.embed.swing.JFXPanel

import org.scalatest.{FreeSpec, Matchers}
import org.scalatest.mockito.MockitoSugar
import org.mockito.Mockito._

import scala.collection.mutable
import scala.util.Random

/**
  * Created by Harambe on 7/6/2017.
  */
class InitPlaceStateAcceptanceTests extends FreeSpec with Matchers with MockitoSugar {

  //Init graphics
  new JFXPanel()

  "Full test through InitPlaceState with 1 human player and 2 computer players" in {
    val humanPlayer = new HumanPlayer
    val computerPlayer = new ComputerPlayer
    val computerPlayer2 = new ComputerPlayer
    val worldMap = new WorldMap(new CountryFactory, List(humanPlayer, computerPlayer, computerPlayer2))

    //Setup
    waitForUserInput(worldMap.baseState.update _)
    worldMap.baseState.forwardState shouldBe Some(InitPlaceState(List(humanPlayer, computerPlayer, computerPlayer2), worldMap.countries))
    humanPlayer.availableArmies shouldBe 35
    computerPlayer.availableArmies shouldBe 35
    computerPlayer2.availableArmies shouldBe 35

    //Placement phase
    var timeout = 0
    while(computerPlayer2.availableArmies != 0 && timeout < 1000){
      clickRandomCountryAndWait(worldMap.countries, worldMap.baseState.update _)
      timeout += 1
    }

    val expectedCountriesOwned = worldMap.countries.size / 3
    val playersWithOneExtra = worldMap.countries.size % 3
    val counOwnerMap = tallyCountryOwnership(worldMap.countries, worldMap.players)
    for(a<-0 until playersWithOneExtra){
      counOwnerMap(worldMap.players(a)) shouldBe expectedCountriesOwned + 1
    }
    for(a<-playersWithOneExtra until worldMap.players.size){
      counOwnerMap(worldMap.players(a)) shouldBe expectedCountriesOwned
    }

    val armyTally = tallyArmies(worldMap.countries, worldMap.players)
    armyTally.foreach(_._2 shouldBe 35)


    //Exit state
    worldMap.baseState.update()
    worldMap.baseState.forwardState shouldBe None
  }


  def tallyArmies(countries:Map[String,Country], players:List[Player]): Map[Player, Int] ={
    val playerMap = mutable.Map[Player, Int]()
    players.foreach(playerMap += _ -> 0)

    countries.foreach(x=> playerMap(x._2.owner.get) = playerMap(x._2.owner.get) + x._2.armies)

    playerMap.toMap
  }


  def tallyCountryOwnership(countries: Map[String,Country], players:List[Player]): Map[Player, Int] ={
    val playerMap = mutable.Map[Player, Int]()
    players.foreach(playerMap += _ -> 0)

    countries.foreach(x=> playerMap(x._2.owner.get) = playerMap(x._2.owner.get) + 1)

    playerMap.toMap
  }

  def clickRandomCountryAndWait(countries: Map[String, Country], updateLoop: ()=> Unit): Unit ={
    countries.values.toList(Random.nextInt(countries.size)).doClickAction()
    waitForUserInput(updateLoop)
  }

  def waitForUserInput(updateLoop:()=>Unit): Unit ={
    for(a<-0 until 20){
      updateLoop()
    }
  }



}
