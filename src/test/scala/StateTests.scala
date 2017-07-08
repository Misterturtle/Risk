import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FreeSpec, Matchers}
import org.mockito.Mockito._

/**
  * Created by Harambe on 6/22/2017.
  */
class StateTests extends FreeSpec with Matchers with MockitoSugar {

  "A state should be able to forward activity to another state" in {
    val forwardState = mock[TestState]
    val forwardConditions = List(()=>true)
    val forwardTrans = new ForwardTransition(forwardConditions, () =>forwardState)
    val ts = new TestState(List(forwardTrans))
    verify(forwardState, times(0)).update()

    ts.update()
    ts.update()

    verify(forwardState, times(1)).update()
  }

  "A state should not forward activity when no transition conditions are met" in {
    val forwardState = mock[TestState]
    val forwardConditions = List(()=>false)
    val forwardTrans = new ForwardTransition(forwardConditions, ()=>forwardState)
    val ts = new TestState(List(forwardTrans))
    verify(forwardState, times(0)).update()

    ts.update()
    ts.update()

    verify(forwardState, times(0)).update()
  }

  "A state should not forward any longer if the forwarded state has a returnState of true" in {
    val forwardTransState = mock[TestState]
    when(forwardTransState.returnState).thenReturn(true)
    val ts = new TestState(Nil){
      _forwardState = Some(()=>forwardTransState)
    }

    ts.update()
    ts.update()
    ts.update()
    ts.update()

    verify(forwardTransState, times(0)).update()
  }

  "When returning from a state, the _returnState flag should be reset" in {
    val forwardTransState = new TestState(Nil){
      _returnState = true
    }
    val ts = new TestState(Nil){
      _forwardState = Some(()=>forwardTransState)
    }

    ts.update()

    ts.forwardState shouldBe None
    forwardTransState.returnState shouldBe false
  }

  "When updating, A state should update it's logic machine after checking for state transitions" in {

    var updateCounter = 0
    val mockLM = new LogicMachine{
      override def update(): Unit ={
        updateCounter += 1
      }
    }
    val ts = new TestState(Nil, mockLM)

    ts.update()
    ts.update()

    updateCounter shouldBe 2
  }

  "When updating, a state should NOT update its logic machine if a state transition is valid" in {
    var updateCounter = 0
    val mockLM = new LogicMachine{
      override def update(): Unit ={
        updateCounter += 1
      }
    }
    val trans = ForwardTransition(List(()=>true), ()=>new TestState(Nil))
    val ts = new TestState(List(trans), mockLM)

    ts.update()
    ts.update()

    updateCounter shouldBe 0
  }

}
