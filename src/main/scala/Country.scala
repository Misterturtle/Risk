import javafx.beans.binding.Bindings
import javafx.beans.property.{ReadOnlyDoubleProperty, SimpleDoubleProperty, SimpleIntegerProperty}
import javafx.beans.value.{ObservableValue, ChangeListener}
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.input.MouseEvent

import scala.util.Random
import scalafx.geometry.Pos
import scalafx.scene.effect.{DropShadow, InnerShadow}
import scalafx.scene.layout._
import scalafx.scene.paint.{Paint, Color}
import scalafx.scene.shape.{Circle, Polygon, Shape}
import scalafx.scene.text.{FontWeight, Text}

/**
  * Created by Harambe on 6/16/2017.
  */
class armyDisplay(armies: SimpleIntegerProperty, countryWidth:ReadOnlyDoubleProperty, countryHeight:ReadOnlyDoubleProperty) extends StackPane {

  private def createCirclePane(): Unit ={
    val circlePane = new HBox()
    circlePane.styleClass.setAll("armyDisplay")
    circlePane.shape = Circle.apply(1)
    circlePane.setMaxSize(Region.USE_PREF_SIZE,Region.USE_PREF_SIZE)
    circlePane.prefHeightProperty().bind(countryWidth.add(countryHeight).divide(7))
    circlePane.prefWidthProperty().bind(countryWidth.add(countryHeight).divide(7))
    circlePane.alignment = Pos.Center
    children.add(circlePane)

    val fontSize = new SimpleDoubleProperty()
    fontSize.bind(width.delegate.add(height.delegate).divide(3))
    val text = new Text()
    text.fill = Color.White
    text.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.asString(), ";", "-fx-text-: white; -fx-font-weight: bolder;"))
    text.textProperty().bind(armies.asString())
    circlePane.children.add(text)
  }

  private def init(): Unit ={
    this.setShape(Circle.apply(1))
    this.setPickOnBounds(false)
    this.setMaxSize(30,30)
    alignment = Pos.Center
    createCirclePane()
  }

  init()
}

class Country(val name: String, val origPoints: List[(Double, Double)]) extends StackPane {

  val origWidth = origPoints.maxBy(_._1)._1 - origPoints.minBy(_._1)._1
  val origHeight = origPoints.maxBy(_._2)._2 - origPoints.minBy(_._2)._2
  val xScale = new SimpleDoubleProperty()
  val yScale = new SimpleDoubleProperty()

  protected var _clickAction = () => {}

  private var _owner:Option[Player] = None
  def owner = _owner
  def setOwner(owner:Player) = _owner = Some(owner)

  private val _armies = new SimpleIntegerProperty()
  def armies = _armies.get()
  def addArmies(amount:Int) = _armies.set(_armies.get() + amount)
  def removeArmies(amount:Int) = _armies.set(_armies.get - amount)


  val ad = new armyDisplay(_armies, this.widthProperty(), this.heightProperty())
  val polygon = new Polygon(new javafx.scene.shape.Polygon())


  def setClickAction(action: ()=>Unit): Unit ={
    _clickAction = action
    polygon.setOnMouseClicked(new EventHandler[MouseEvent] {
      override def handle(event: MouseEvent): Unit = action()
    })
  }

  def getClickAction(): ()=>Unit = _clickAction

  def initShape(parentXScale:SimpleDoubleProperty, parentYScale:SimpleDoubleProperty): Unit ={
    origPoints.foreach{case (x,y) => polygon.getPoints.addAll(x,y)}
    children.add(polygon)
    children.add(ad)
    xScale.bind(parentXScale)
    yScale.bind(parentYScale)
    styleClass.setAll("country")
    polygon.setFill(Color.rgb(200,0,0,.3))
    polygon.toFront()
    this.setShape(polygon)
    this.setPickOnBounds(false)
  }

  def resizePoly(): Unit ={
    polygon.getPoints.removeAll(polygon.points)
    origPoints.foreach{case (x,y)=> polygon.points.addAll(x * xScale.doubleValue(), y * yScale.doubleValue())}
  }

  def drawDebug(): Unit ={
    val listOfColors = List[Color](
      Color.Red,
      Color.Blue,
      Color.Green,
      Color.Beige,
      Color.Black,
      Color.White,
      Color.Yellow,
      Color.Orange,
      Color.Gold
    )
    polygon.setFill(listOfColors(Random.nextInt(9)))
  }


}
