/**
  * Created by Harambe on 6/16/2017.
  */
case class Transition(conditions: List[()=>Boolean], effects: List[()=>Unit], newState: State)

case class State(transitions:List[Transition] = Nil) {

  def checkTransitions(): (List[() => Unit], State) = ???

  def registerTransition(newTransition: Transition): State = copy(newTransition :: transitions)


}
