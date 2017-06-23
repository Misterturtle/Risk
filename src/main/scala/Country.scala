import javafx.event.EventHandler
import javafx.scene.input.MouseEvent

/**
  * Created by Harambe on 6/16/2017.
  */
class Country(val name: String, val origPoints: List[(Double, Double)]) extends javafx.scene.shape.Polygon() {

  protected var _clickAction = () => {}

  private var _owner:Option[Player] = None
  def owner = _owner
  def setOwner(owner:Player) = _owner = Some(owner)

  private var _armies = 0
  def armies = _armies
  def addArmies(amount:Int) = _armies += amount
  def removeArmies(amount:Int) = _armies -= amount

  def setClickAction(action: ()=>Unit): Unit ={
    _clickAction = action
    this.setOnMouseClicked(new EventHandler[MouseEvent] {
      override def handle(event: MouseEvent): Unit = action()
    })
  }

  def getClickAction(): ()=>Unit = _clickAction





}
