/**
  * Created by Harambe on 6/16/2017.
  */
case class Transition(conditions: List[() => Boolean], effects: List[() => Unit], newState: () => State, popOldState: Boolean)

case class State(transitions:List[Transition] = Nil) {

  def update(): Option[Transition] = transitions.find(_.conditions.forall(_()))

  def registerTransition(newTransition: Transition): State = copy(newTransition :: transitions)


}
