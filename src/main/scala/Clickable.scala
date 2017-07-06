/**
  * Created by Harambe on 7/1/2017.
  */
trait Clickable {
  protected var _clickAction = () => {}
  def setClickAction(action: ()=> Unit): Unit = _clickAction = action
  def getClickAction() = _clickAction
  def doClickAction(): Unit = getClickAction()()
}
