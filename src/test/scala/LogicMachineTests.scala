import org.scalatest.{FreeSpec, Matchers}

/**
  * Created by Harambe on 6/23/2017.
  */
class LogicMachineTests extends FreeSpec with Matchers {

  "When updated, a Logic Machine should check all conditions and execute effects if conditions are met" in {

    var effectFired = 0
    val event = LogicEvent(List(()=>true), List(() =>effectFired += 1))
    val lm = new LogicMachine(List(event))

    lm.update()
    lm.update()

    effectFired shouldBe 2
  }

  "When updated, a Logic Machine should NOT execute effects if even 1 condition is not met" in {

    var effectFired = 0
    val event = LogicEvent(List(()=>true, ()=> false), List(() =>effectFired += 1))
    val lm = new LogicMachine(List(event))

    lm.update()
    lm.update()

    effectFired shouldBe 0
  }


}
