import scala.collection.mutable.ListBuffer

/**
  * Created by Harambe on 6/23/2017.
  */

case class LogicEvent(val conditions: List[()=>Boolean], val effects: List[()=>Unit])


class LogicMachine() {

  private var events = ListBuffer[LogicEvent]()

  def addEvent(event: LogicEvent): Unit ={
    events.append(event)
  }

  def update(): Unit ={
    events.foreach{
      case LogicEvent(conds, effects) =>
        if(conds.forall(_()))
          effects.foreach(_())

      case _ =>
    }
  }
}
