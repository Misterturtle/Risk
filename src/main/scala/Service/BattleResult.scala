package Service

import scala.collection.mutable.ListBuffer
import scalaz.Scalaz

/**
  * Created by Harambe on 7/20/2017.
  */
case class BattleResult(offRolls:List[Int] = Nil, defRolls:List[Int] = Nil, rGen: RandomFactory){
  import Scalaz._

  private val _offRolls: ListBuffer[Int] = ListBuffer[Int]()
  private val _defRolls: ListBuffer[Int] = ListBuffer[Int]()

  def attack(offensiveArmies:Int, defensiveArmies:Int):BattleResult = {
    for(a<-0 until offensiveArmies){
      _offRolls.append(rGen.roll())
    }
    for(a<-0 until defensiveArmies){
      _defRolls.append(rGen.roll())
    }

    copy(offRolls = _offRolls.toList, defRolls = _defRolls.toList)
  }

  def offDefArmiesLost(): (Int,Int) = {
    val sortedOffRolls = offRolls.sorted.reverse
    val sortedDefRolls = defRolls.sorted.reverse
    var offArmiesLost = 0
    var defArmiesLost = 0
    for(a<- sortedDefRolls.indices){
      (sortedDefRolls(a) >= sortedOffRolls(a)) ? (offArmiesLost += 1) | (defArmiesLost += 1)
    }

    (offArmiesLost, defArmiesLost)
  }
}
