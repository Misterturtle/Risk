package Sugar

import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.event.{ActionEvent, EventHandler}
import javafx.scene.input.MouseEvent

import GUI.CustomEH


/**
  * Created by Harambe on 8/17/2017.
  */
object CustomSugar {
  implicit def mouseEvent2EventHandler(event:(MouseEvent)=>Unit):EventHandler[MouseEvent]= new EventHandler[MouseEvent]{
    override def handle(dEvent:MouseEvent):Unit = event(dEvent)
  }

  implicit def actionEvent2EventHandler(event:(ActionEvent)=>Unit):EventHandler[ActionEvent]= new EventHandler[ActionEvent]{
    override def handle(dEvent:ActionEvent):Unit = event(dEvent)
  }

  implicit def uselessEvent2ActionEventHandler(event:()=>Unit):EventHandler[ActionEvent] = new EventHandler[ActionEvent]{
      override def handle(dEvent: ActionEvent): Unit = event()}

  implicit def uselessEvent2MouseEventHandler(event:()=>Unit):EventHandler[MouseEvent] = new EventHandler[MouseEvent]{
    override def handle(dEvent: MouseEvent): Unit = {
      event()
      CustomEH.consumeEvent(dEvent)
    }}


  class BoolWrapper(value:Boolean){
    def and(second:Boolean): Boolean = {
      value && second
    }
  }

  implicit def bool2BoolWrapper(bool:Boolean): BoolWrapper = new BoolWrapper(bool)


}



