import javafx.embed.swing.JFXPanel
import javax.swing.JPanel

import Service._
import org.scalatest.{FreeSpec, Matchers}

/**
  * Created by Harambe on 7/18/2017.
  */
class EndToEndAcceptanceTest extends FreeSpec with Matchers {

  //Init JavaFX graphics
  new JFXPanel

  val wmCont = new WorldMapController()

  "One gigantuous end to end test" in {



    // When a new game begins,
    wmCont.sideEffectManager.performServiceEffect(Effects.begin(wmCont.getCurrentWorldMap))
    val wm = wmCont.getCurrentWorldMap

    //    No countries should be owned
    wm.countries.forall(_.owner.isEmpty) shouldBe true

    //    There should be 42 countries
    wm.countries.size shouldBe 42

    //    There should be 3 players
    wm.players.size shouldBe 3


    //    Each player should have 35 armies
    wm.players.forall(_.armies == 35)

    //  The Service.WorldMap should be in InitialPlacementPhase
    wm.phase shouldBe InitialPlacement

    //    The active player should be the first player
    wm.activePlayerNumber shouldBe 1

    //  During the InitialPlacement phase"


    //    Clicking a country should
    clickOnCountry(wm, "alaska")
    val postClickWM = wmCont.getCurrentWorldMap
    val postClickAlaska = postClickWM.countries.find(_.name == "alaska").get

    //      Place an army on the country
    postClickAlaska.armies shouldBe 1

    //      Set the owner of a country
    postClickAlaska.owner.get.name shouldBe postClickWM.getPlayerByPlayerNumber(1).get.name

    //      Remove an army from the active player
    postClickWM.getPlayerByPlayerNumber(1).get.armies shouldBe 34

    //      Change the active player to the 2nd player
    postClickWM.activePlayerNumber shouldBe 2

    //    Clicking a 2nd country should
    clickOnCountry(wm, "argentina")
    val postClickWM2 = wmCont.getCurrentWorldMap
    val postClickArgentina = postClickWM2.countries.find(_.name == "argentina").get

    //      Place an army on the country
    postClickArgentina.armies shouldBe 1

    //      Set the owner of a country
    postClickArgentina.owner.get.name shouldBe postClickWM2.getPlayerByPlayerNumber(2).get.name

    //     Remove an army from the active player
    postClickWM2.getPlayerByPlayerNumber(2).get.armies shouldBe 34

    //      Cause the computer player to take his entire turn
    val comp = postClickWM2.getPlayerByPlayerNumber(3).get
    comp.armies shouldBe 34
    postClickWM2.countries.count(_.owner.map(_.name).contains(comp.name)) shouldBe 1
    postClickWM2.countries.find(_.owner.map(_.name).contains(comp.name)).get.armies shouldBe 1

    //      Cycle the turn back to the first player
    postClickWM2.activePlayerNumber shouldBe 1

    //    Before claiming all countries
    val preclaimWM = wmCont.getCurrentWorldMap

    //      Clicking a self owned country before all countries owned should do nothing
    preclaimWM.countries.count(_.owner.map(_.name).contains("Turtle")) shouldBe 1
    clickOnCountry(preclaimWM, "alaska")
    wmCont.getCurrentWorldMap shouldBe preclaimWM

    //      Clicking a country you don't own should do nothing
    clickOnCountry(preclaimWM, "argentina")
    wmCont.getCurrentWorldMap shouldBe preclaimWM


    //    After all countries have been claimed
    //39 countries left at this point. 13 more full turns to claim them all. 26 clicks between the 2 human players
    for (a <- 1 to 26) {
      val unownedCountry = wmCont.getCurrentWorldMap.countries.find(_.owner.isEmpty).get
      clickOnCountry(preclaimWM, unownedCountry.name)
    }
    val postClaimWM = wmCont.getCurrentWorldMap

    //      No countries should be unowned
    postClaimWM.countries.forall(_.owner.nonEmpty) shouldBe true

    //      Each player should own 14 countries
    postClaimWM.countries.count(_.owner.map(_.playerNumber).contains(1)) shouldBe 14
    postClaimWM.countries.count(_.owner.map(_.playerNumber).contains(2)) shouldBe 14
    postClaimWM.countries.count(_.owner.map(_.playerNumber).contains(3)) shouldBe 14

    //      It should be the first players turn
    postClaimWM.activePlayerNumber shouldBe 1

    //      Clicking on a non-owned country should do nothing
    val nonownedCountry = postClaimWM.countries.find(_.owner.map(_.playerNumber).contains(2)).get
    clickOnCountry(postClaimWM, nonownedCountry.name)
    wmCont.getCurrentWorldMap shouldBe postClaimWM

    //      Clicking on an owned country should
    val ownedCountry = postClaimWM.countries.find(_.owner.map(_.playerNumber).contains(1)).get
    clickOnCountry(postClaimWM, ownedCountry.name)
    val placed2ndArmyWM = wmCont.getCurrentWorldMap

    //      Place a 2nd army
    placed2ndArmyWM.countries.find(_.name == ownedCountry.name).get.armies shouldBe 2

    //      Remove an army from the player
    placed2ndArmyWM.players.find(_.playerNumber == 1).get.armies shouldBe 20

    //      Change to the 2nd players turn
    placed2ndArmyWM.activePlayerNumber shouldBe 2

    //      After the 2nd player places his army
    val ownedCountry2 = placed2ndArmyWM.countries.find(_.owner.map(_.playerNumber).contains(2)).get
    clickOnCountry(placed2ndArmyWM, ownedCountry2.name)
    val secondPlayerPlaced2ndArmyWm = wmCont.getCurrentWorldMap

    //      Place a 2nd army
    secondPlayerPlaced2ndArmyWm.countries.find(_.name == ownedCountry2.name).get.armies shouldBe 2

    //      Remove an army from the player
    secondPlayerPlaced2ndArmyWm.players.find(_.playerNumber == 2).get.armies shouldBe 20

    //      The computer should take his turn
    secondPlayerPlaced2ndArmyWm.getPlayerByPlayerNumber(3).get.armies shouldBe 20
    secondPlayerPlaced2ndArmyWm.countries.count(c => c.owner.map(_.playerNumber).get == 3 && c.armies == 2) shouldBe 1

    //      Cycle back to the 1st players turn
    secondPlayerPlaced2ndArmyWm.activePlayerNumber shouldBe 1

    //      Place armies until the Init Place Service.Phase is over
    val firstPlayersCountry = secondPlayerPlaced2ndArmyWm.getCountry("alaska")
    val secondPlayersCountry = secondPlayerPlaced2ndArmyWm.getCountry("argentina")
    for (a <- 1 to 20) {
      clickOnCountry(wmCont.getCurrentWorldMap, firstPlayersCountry.name)
      clickOnCountry(wmCont.getCurrentWorldMap, secondPlayersCountry.name)
    }
    val turnPlacementWM = wmCont.getCurrentWorldMap

    //The phase should be Turn Placement
    turnPlacementWM.phase shouldBe TurnPlacement

    //It should be the first players turn
    turnPlacementWM.activePlayerNumber shouldBe 1

    //The first player should have 4 armies allocated to him for his first turn
    turnPlacementWM.getActivePlayer.get.armies shouldBe 4

    //Clicking a non-owned country should do nothing
    clickOnCountry(turnPlacementWM, "argentina")
    wmCont.getCurrentWorldMap shouldBe turnPlacementWM

    //Clicking on an owned country during Turn Placement
    val armiesBeforeClick = turnPlacementWM.getCountry("alaska").armies
    clickOnCountry(wmCont.getCurrentWorldMap, "alaska")

    //should place the army
    wmCont.getCurrentWorldMap.getCountry("alaska").armies shouldBe armiesBeforeClick + 1
    wmCont.getCurrentWorldMap.getActivePlayer.get.armies shouldBe 3

    //Placing the rest of your armies
    for (a <- 1 to 3) {
      clickOnCountry(wmCont.getCurrentWorldMap, "alaska")
    }

    //Should transition to attack phase
    wmCont.getCurrentWorldMap.phase shouldBe Attacking(None, None)
  }




  def clickOnCountry(wm:WorldMap, countryName:String):Unit = {
    wmCont.wmUICont.receiveInput(CountryClicked(wm.countries.find(_.name == countryName).get))
  }


}



