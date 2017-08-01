package Service

import javafx.scene.paint.{Paint, Color}

/**
  * Created by Harambe on 7/20/2017.
  */
trait UIController[A] {
  def receiveInput(input:Input): Unit
  val get: () => A
}


class WorldMapUIController(val get:()=>WorldMap, sideEffectManager: SideEffectManager) extends UIController[WorldMap] {

  def receiveInput(input:Input): Unit = {
    input match {
      case CountryClicked(country) =>
        sideEffectManager.performEffect(Effects.getCountryClickedEffect(get(), country))
      case ConfirmBattle(source,target,offenseArmies) =>
        sideEffectManager.performEffect(Effects.executeBattle(get(), ConfirmBattle(source, target, offenseArmies)))
      case ConfirmTransfer(amount) =>
        sideEffectManager.performEffect(Effects.executeBattleTransfer(get(), ConfirmTransfer(amount)))
      case Retreat =>
        sideEffectManager.performEffect(Effects.retreatFromBattle(get()))
      case EndAttackPhase =>
        sideEffectManager.performEffect(Effects.endAttackPhase(get()))
    }
  }


  def getCurrPlayersName: String = get().getActivePlayer.map(_.name).getOrElse("Invalid Player")

  def getCurrPlayersArmies: Int = get().getActivePlayer.map(_.armies).getOrElse(-1)

  def getCurrPlayersColor: Paint = get().getActivePlayer.map(_.color).getOrElse(Color.TRANSPARENT)

  def getCurrPlayersTerritories: Int = get().countries.count(_.owner == get().getActivePlayer)

  def getCountries: List[Country] = get().countries

  def getCountryByName(name:String) = get().getCountry(name)

  def getPhase: Phase = get().phase



}
