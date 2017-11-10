package Service

import scala.util.Random

/**
  * Created by Harambe on 7/19/2017.
  */
class RandomFactory {
  val rGen = new Random()

  def roll():Int = rGen.nextInt(6)+1

  def cardIndex(deckSize:Int):Int = rGen.nextInt(deckSize)
}
