import org.scalatest.{FreeSpec, Matchers}

/**
  * Created by Harambe on 6/22/2017.
  */
class StateTests extends FreeSpec with Matchers {

  "A state should be able to forward activity to another state" in {

    var hasForwarded = false
    val forwardState = new TestState(Nil){
      override def update(): Unit = {
        hasForwarded = true
      }
    }


    val forwardConditions = List(()=>true)
    val forwardTrans = new ForwardTransition(forwardConditions, forwardState)
    val ts = new TestState(List(forwardTrans))
    hasForwarded shouldBe false

    ts.update()
    ts.update()

    hasForwarded shouldBe true
  }

  "A state should not forward activity when no transition conditions are met" in {
    var hasForwarded = false
    val forwardState = new TestState(Nil){
      override def update(): Unit = {
        hasForwarded = true
      }
    }

    val forwardConditions = List(()=>false)
    val forwardTrans = new ForwardTransition(forwardConditions, forwardState)
    val ts = new TestState(List(forwardTrans))
    hasForwarded shouldBe false

    ts.update()
    ts.update()

    hasForwarded shouldBe false
  }

  "A state should not forward any longer if the forwarded state has a returnState of true" in {

    var forwardStateUpdated = false

    val forwardTransState = new TestState(Nil){
      _returnState = true

      override def update():Unit = {
        forwardStateUpdated = true
      }
    }
    val ts = new TestState(Nil){
      _forwardState = Some(forwardTransState)
    }

    ts.update()
    ts.update()
    ts.update()
    ts.update()

    forwardStateUpdated shouldBe false
  }

}
