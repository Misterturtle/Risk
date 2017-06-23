/**
  * Created by Harambe on 6/23/2017.
  */

case class LogicEvent(val conditions: List[()=>Boolean], val effects: List[()=>Unit])


class LogicMachine(events: List[LogicEvent]) {

  def update(): Unit ={
    events.foreach{
      case LogicEvent(conds, effects) =>
        if(conds.forall(_()))
          effects.foreach(_())

      case _ =>
    }
  }
}
