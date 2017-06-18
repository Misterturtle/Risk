import org.scalatest.{FreeSpec, Matchers}

import scala.collection.mutable.ListBuffer

/**
  * Created by Harambe on 6/16/2017.
  */
class FSMTests extends FreeSpec with Matchers {



  "A FSM should be able to push a state to the state stack" in {

    val fsm = new FSM
    val mockState = State()

    fsm.push(mockState)

    fsm.getState() shouldBe Some(mockState)
  }

  "A FSM should be able to pop a state from the state stack" in {

    val fsm = new FSM
    val mockState = State()
    val mockState2 = State()
    fsm.push(mockState)
    fsm.push(mockState2)
    fsm.getState() shouldBe Some(mockState2)

    fsm.pop()

    fsm.getState() shouldBe Some(mockState)
  }

  "A FSM should return None as current state is the stack is empty" in {
    val fsm = new FSM

    fsm.getState() shouldBe None
  }

  "A FSM should be able to handle a pop call on an empty stack" in {
    val fsm = new FSM

    noException should be thrownBy fsm.pop()
  }


  "A state should be able register a condition/effect transition" in {
    val fsm = new FSM
    var state = State(Nil)
    val conditionVar = false
    var effectVar = false
    val conditionList = List(()=>conditionVar)
    val effectList = List(()=>effectVar = true)

    val trans = Transition(conditionList, effectList, state)

    state = state.registerTransition(trans)

    state shouldBe State(List(trans))
  }


  //  "A FSM should check the active state's transition conditions" in {
  //
  //    val fsm = new FSM
  //    val mockState = State(){
  //      var test = 1
  //      override def checkTransitions(): (List[() => Unit], State) = {
  //        (List(() => {test += 1}), this)
  //      }
  //    }
  //    fsm.push(mockState)
  //
  //    fsm.update()
  //
  //
  //  }
}
