import scala.collection.mutable.ListBuffer

/**
  * Created by Harambe on 6/16/2017.
  */
class FSM {

  private val stack = new ListBuffer[() => State]()

  def push(stateRef: () => State): Unit = {
    stack.append(stateRef)
  }

  def pop(): Unit = {
    if (stack.nonEmpty)
      stack.remove(stack.size - 1)
  }

  def getState: Option[State] = stack.lastOption.map(_ ())

  def update(): Unit = {
    stack.lastOption match {
      case Some(stateRef) =>
        stateRef().update() match {
          case Some(transition) =>
            transition.effects.foreach {
              _ ()
            }
            if (transition.popOldState)
              pop()
            push(transition.newStateRef)

          case None =>
        }
      case None =>
    }
  }


}
