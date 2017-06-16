import org.scalatest.{FreeSpec, Matchers}

import scala.collection.mutable.ListBuffer

/**
  * Created by Harambe on 6/16/2017.
  */
class FSMTests extends FreeSpec with Matchers {



  "A FSM should be able to push a state to the state stack" in {

    val fsm = new FSM
    val mockState = new State()

    fsm.push(mockState)

    fsm.getState() shouldBe Some(mockState)
  }

  "A FSM should be able to pop a state from the state stack" in {

    val fsm = new FSM
    val mockState = new State()
    val mockState2 = new State()
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

    fsm.pop()
  }

//  "A FSM should check the active state's transition conditions" in {
//
//    val fsm = new FSM
//    val mockState = new State(){
//      def
//    }
//
//
//  }



}
