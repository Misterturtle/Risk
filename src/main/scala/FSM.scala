import scala.collection.mutable.ListBuffer

/**
  * Created by Harambe on 6/16/2017.
  */
class FSM {

  private val stack = new ListBuffer[State]()

  def push(state:State): Unit ={
    stack.append(state)
  }

  def pop(): Unit ={
    if(stack.nonEmpty)
      stack.remove(stack.size-1)
  }

  def getState(): Option[State] = stack.lastOption

  def update(): Unit ={
    stack.last.update() match{
      case Some(transition)=>
        transition.effects.foreach{_()}
        if(transition.popOldState)
          pop()
        push(transition.newState())

      case None =>
    }
  }




}
