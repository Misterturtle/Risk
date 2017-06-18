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
    var state = State(Nil)
    val conditionVar = false
    var effectVar = false
    val conditionList = List(()=>conditionVar)
    val effectList = List(()=>effectVar = true)

    val trans = Transition(conditionList, effectList, ()=>state, false)

    state = state.registerTransition(trans)

    state shouldBe State(List(trans))
  }

  "When updating a state, it should return None if no transition conditions are met" in {
    val conditionVar = true
    val conditionVar2 = false
    var effectVar = false
    val conditionList = List(()=>conditionVar, ()=>conditionVar2)
    val effectList = List(()=>effectVar = true)
    val trans:Transition = Transition(conditionList, effectList, ()=>State(), false)
    val state = State(List(trans))

    state.update() shouldBe None
  }

  "When updating a state, it should return a transition if all conditions of a transition are met" in{
    val conditionVar = true
    val conditionVar2 = true
    var effectVar = false
    val conditionList = List(()=>conditionVar, ()=>conditionVar2)
    val effectList = List(()=>effectVar = true)
    val trans = Transition(conditionList, effectList, ()=>State(), false)
    val state = State(List(trans))

    state.update() shouldBe Some(trans)
  }


    "A FSM should execute the state's transition effects if a transition is returned from their update" in {
      var effectCounter = 0

      val fsm = new FSM {
        var state1 = State(Nil)
        val trans = Transition(List(()=>true), List(()=>{effectCounter +=1}, ()=> {effectCounter+= 1}), ()=>state1, false)
        state1 = state1.registerTransition(trans)
      }
      fsm.push(fsm.state1)

      fsm.update()

      effectCounter shouldBe 2
    }



}
