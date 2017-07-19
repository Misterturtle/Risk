import javafx.beans.binding.Bindings
import javafx.beans.property.{SimpleStringProperty, ReadOnlyDoubleProperty, SimpleDoubleProperty, SimpleIntegerProperty}
import javafx.beans.value.{ObservableValue, ChangeListener}
import javafx.event.EventHandler
import javafx.scene.input.MouseEvent

import scala.util.Random
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.effect.{DropShadow, InnerShadow}
import scalafx.scene.layout._
import scalafx.scene.paint.{Paint, Color}
import scalafx.scene.shape.{Circle, Polygon, Shape}
import scalafx.scene.text.{FontWeight, Text}

/**
  * Created by Harambe on 6/16/2017.
  */
class armyDisplay(armies: String, color:String, countryWidth: ReadOnlyDoubleProperty, countryHeight: ReadOnlyDoubleProperty) extends StackPane {

  val circlePane = new HBox()
  val cpText = new Text()

  private def createCirclePane(): Unit = {

    circlePane.styleClass.add(0, "armyDisplay")
    circlePane.styleClass.add(1, color+"Background")

    circlePane.shape = Circle.apply(1)

    circlePane.maxHeightProperty().bind(countryWidth.add(countryHeight).divide(7))
    circlePane.maxWidthProperty().bind(countryWidth.add(countryHeight).divide(7))

    circlePane.prefHeightProperty().bind(countryWidth.add(countryHeight).divide(7))
    circlePane.prefWidthProperty().bind(countryWidth.add(countryHeight).divide(7))

    //circlePane.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE)
    circlePane.alignment = Pos.Center
    children.add(circlePane)

    val fontSize = new SimpleDoubleProperty()
    fontSize.bind(width.delegate.add(height.delegate).divide(3))

    cpText.fill = Color.White
    cpText.text = armies
    cpText.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.asString(), ";", "-fx-text-: white; -fx-font-weight: bolder;"))
    circlePane.children.add(cpText)
  }

  def update(armies: Int, color:String): Unit ={

    circlePane.styleClass.remove(1)
    circlePane.styleClass.add(1, color+"Background")
    cpText.text = armies.toString
  }

  private def init(): Unit = {
    this.setShape(Circle.apply(1))
    this.setPickOnBounds(false)
    this.setMaxSize(30, 30)
    this.mouseTransparent = true
    alignment = Pos.Center
    createCirclePane()
  }



  init()
}


class CountryUI(startingCountry:Country, val origPoints: List[(Double, Double)], wmInputHandler: WorldMapInputHandler) extends StackPane {


  val origWidth = if (origPoints.nonEmpty) origPoints.maxBy(_._1)._1 - origPoints.minBy(_._1)._1 else 0
  val origHeight = if (origPoints.nonEmpty) origPoints.maxBy(_._2)._2 - origPoints.minBy(_._2)._2 else 0
  val xScale = new SimpleDoubleProperty()
  val yScale = new SimpleDoubleProperty()

  val ad = new armyDisplay(startingCountry.armies.toString, startingCountry.owner.map(_.color).getOrElse("gray"), this.widthProperty(), this.heightProperty())
  val polygon = new Polygon(new javafx.scene.shape.Polygon())

  def update(country: Country): Unit = {
    ad.update(country.armies, country.owner.map(_.color).getOrElse("gray"))
    polygon.onMouseClicked = new EventHandler[MouseEvent] {
      override def handle(event: MouseEvent): Unit =  wmInputHandler.receiveInput(CountryClicked(country))
    }
  }


  def initShape(parentXScale: SimpleDoubleProperty, parentYScale: SimpleDoubleProperty): Unit = {
    origPoints.foreach { case (x, y) => polygon.getPoints.addAll(x, y) }
    xScale.bind(parentXScale)
    yScale.bind(parentYScale)


    styleClass.setAll("country")
    polygon.setFill(Color.Transparent)

    polygon.toFront()
    this.setShape(polygon)
    this.setPickOnBounds(false)
    polygon.setOnMouseClicked(new EventHandler[MouseEvent] {
      override def handle(event: MouseEvent): Unit = wmInputHandler.receiveInput(CountryClicked(startingCountry))
    })
    children.add(polygon)
    children.add(ad)
  }


  def resizePoly(): Unit = {
    polygon.getPoints.removeAll(polygon.points)
    origPoints.foreach { case (x, y) => polygon.points.addAll(x * xScale.doubleValue(), y * yScale.doubleValue()) }
  }

  def drawDebug(): Unit = {
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




