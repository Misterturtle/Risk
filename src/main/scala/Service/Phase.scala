package Service

/**
  * Created by Harambe on 7/20/2017.
  */
trait Phase

case object NotInGame extends Phase

case object InitialPlacement extends Phase

case object TurnPlacement extends Phase

case class Attacking(source:Option[Country], previousBattleResults:Option[BattleResult]) extends Phase {
  def setSource(country:Country) = copy(source = Some(country))
}


case class Battle(source:Country, target:Country, previousBattle:Option[BattleResult] = None, isTransferring:Boolean = false) extends Phase

case class Reinforcement(source:Option[Country], target:Option[Country]) extends Phase