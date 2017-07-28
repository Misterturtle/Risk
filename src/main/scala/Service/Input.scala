package Service

/**
  * Created by Harambe on 7/17/2017.
  */
trait Input

case class ConfirmBattle(source:Country, target:Country, offenseArmies:Int) extends Input

case class ConfirmTransfer(amount:Int) extends Input

case class CountryClicked(country:Country) extends Input

case object EndAttackPhase extends Input

case object Retreat extends Input
