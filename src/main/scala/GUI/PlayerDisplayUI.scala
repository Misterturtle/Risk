package GUI

import  javafx.beans.value.{ObservableValue, ChangeListener}
import javafx.beans.binding.DoubleBinding
import javafx.beans.property.ReadOnlyDoubleProperty
import javafx.event.{ActionEvent, EventHandler}
import javafx.geometry.Insets
import javafx.scene.layout.{Background, BackgroundFill, CornerRadii}
import javafx.scene.paint.{Paint, Color}

import Service._

import scalafx.animation.TranslateTransition
import scalafx.geometry
import scalafx.geometry.{Pos}
import scalafx.scene.Group
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{AnchorPane, Priority, HBox, VBox}
import scalafx.scene.text.Text
import scalafx.util.Duration

import Sugar.CustomSugar._

/**
  * Created by Harambe on 7/20/2017.
  */
class PlayerDisplayUI() extends VBox with PlayerListener with CountryListener with UIController{

  val self = this

  private val displayAnim = new TranslateTransition(new Duration(300), this)
  private var _isDisplayed = false

  val nameText = new Text()
  nameText.scaleX = 2
  nameText.scaleY = 2
  nameText.styleClass.add("defaultText")

  val nameContainer = new HBox()
  nameContainer.prefWidth.bind(prefWidth)
  nameContainer.prefHeight.bind(prefHeight.divide(2))

  nameContainer.alignment = Pos.Center
  nameContainer.children.add(new Group(nameText))

  val statsBar = new StatsBar(CustomColors.gray, 0, 0)
  statsBar.alignment = Pos.CenterLeft



  this.setTranslateX(-prefWidth.get)
  alignment = Pos.TopLeft
  children.addAll(nameContainer, new Group(statsBar))


  def update(): Unit ={
      statsBar.update(_player.value.color, _player.value.armies, _countries.value.count(_.owner.map(_.playerNumber).contains(_player.value.playerNumber)), _player.value.cards.size)
      nameText.setText(_player.value.name)
      nameContainer.setBackground(new Background(new BackgroundFill(_player.value.color, new CornerRadii(0,0,100,100, false), Insets.EMPTY)))
  }

  def openAnim(): Unit = {
    _isDisplayed = true
    displayAnim.setToX(0)
    this.toFront()
    displayAnim.playFromStart()
  }

  def closeAnim(onFinish: () => Unit): Unit ={
    _isDisplayed = false
    displayAnim.setOnFinished(onFinish)
    displayAnim.setToX(-this.prefWidth.get)
    displayAnim.playFromStart()
  }

  override def onPlayerChange(oldPlayer: Player, newPlayer: Player): () => Unit = () => {
    val nullOldPlayer = oldPlayer == null
    val isDiffPlayer = if(!nullOldPlayer) oldPlayer.playerNumber != newPlayer.playerNumber else false
    val isSamePlayer = if(!nullOldPlayer) oldPlayer.playerNumber == newPlayer.playerNumber else false

    Unit match {

      case _ if nullOldPlayer =>
        update()

      case _ if isDiffPlayer =>
        closeAnim(() => {
          update()
          openAnim()
        })

      case _ if isSamePlayer =>
        update()
    }
  }

  override def onCountryChange(oldCountries: List[Country], newCountries: List[Country]): () => Unit = () => {}

  override def anchorResize(sceneWidth: ReadOnlyDoubleProperty, sceneHeight: ReadOnlyDoubleProperty): Unit = {}

  override def bindScale(windowScaleX: DoubleBinding, windowScaleY: DoubleBinding): Unit = {
    statsBar.scaleX.bind(windowScaleX)
    statsBar.scaleY.bind(windowScaleY)
  }

  override def bindPrefSize(sceneWidth: ReadOnlyDoubleProperty, sceneHeight: ReadOnlyDoubleProperty): Unit = {
    prefHeight.bind(sceneHeight.divide(10))
    prefWidth.bind(sceneWidth.divide(4))
  }

  override def init(sceneWidth: ReadOnlyDoubleProperty, sceneHeight: ReadOnlyDoubleProperty): Unit = {}
}


class StatsBar(initColor:Paint, initArmyAmount:Int, initTerritoryAmount:Int) extends HBox{

  val redArmies = new Image(getClass.getResourceAsStream("/red_army.png"))
  val yellowArmies = new Image(getClass.getResourceAsStream("/yellow_army.png"))
  val blueArmies = new Image(getClass.getResourceAsStream("/blue_army.png"))
  val blackArmies = new Image(getClass.getResourceAsStream("/black_army.png"))
  val greenArmies = new Image(getClass.getResourceAsStream("/green_army.png"))
  val brownArmies = new Image(getClass.getResourceAsStream("/brown_army.png"))
  val pinkArmies = new Image(getClass.getResourceAsStream("/pink_army.png"))

  val colorMap = Map[Paint,Image](
    CustomColors.red -> redArmies,
    CustomColors.green -> greenArmies,
    CustomColors.black -> blackArmies,
    CustomColors.yellow -> yellowArmies,
    CustomColors.brown -> brownArmies,
    CustomColors.blue -> blueArmies,
    CustomColors.pink -> pinkArmies,
    CustomColors.gray -> new Image(getClass.getResourceAsStream("/errorBox.png")),
    Color.TRANSPARENT -> new Image(getClass.getResourceAsStream("/errorBox.png"))
  )


  val armiesTab = new StatTab(colorMap(initColor), initArmyAmount)
  armiesTab.prefHeight.bind(this.prefHeightProperty())
  armiesTab.prefWidth.bind(this.prefWidthProperty())

  val territoriesTab = new StatTab(new Image(getClass.getResourceAsStream("/africa.png")), initTerritoryAmount)
  territoriesTab.prefHeight.bind(this.prefHeightProperty())
  territoriesTab.prefWidth.bind(this.prefWidthProperty())


  val cardTab = new HBox()
  cardTab.prefHeight.bind(this.prefHeightProperty())
  cardTab.prefWidth.bind(this.prefWidthProperty())

  val cardUI = new CardUI(new Card("Alaska", Infantry))
  cardUI.scaleX = .15
  cardUI.scaleY = .15
  cardUI.alignment = Pos.Center

  val cardText = new Text("0")
  cardText.styleClass.add("defaultText")
  cardText.setScaleX(2)
  cardText.setScaleY(2)
  val textContainer = new HBox()
  textContainer.alignment = Pos.Center
  textContainer.children.add(cardText)

  cardTab.spacing = 20
  cardTab.alignment = Pos.Center
  cardTab.padding = scalafx.geometry.Insets(0,20,0,20)
  cardTab.children.addAll(new Group(cardUI), new Group(textContainer))

  children.addAll(armiesTab, territoriesTab, cardTab)

  def update(color:Paint, armyAmount: Int, territoryAmount: Int, cardAmount: Int): Unit ={
    armiesTab.update(colorMap(color), armyAmount)
    territoriesTab.update(newValue = territoryAmount)
    cardText.setText(cardAmount.toString)
  }
}



class StatTab(image:Image, value:Int) extends HBox {

  val tabImage = new ImageView(image)
  tabImage.preserveRatio = true
  tabImage.fitHeight = 40

  val imageContainer = new HBox()
  imageContainer.children.add(tabImage)
  imageContainer.alignment = Pos.Center
  imageContainer.padding = scalafx.geometry.Insets(0,20,0,0)


  val tabText = new Text(value.toString)
  tabText.styleClass.add("defaultText")
  tabText.setScaleX(2)
  tabText.setScaleY(2)
  val textContainer = new HBox()
  textContainer.alignment = Pos.Center
  textContainer.children.add(tabText)

  alignment = Pos.Center
  padding = scalafx.geometry.Insets(0,20,0,20)
  children.addAll(imageContainer, new Group(textContainer))

  def update(newImage:Image = new Image(tabImage.getImage), newValue:Int): Unit = {
    tabImage.setImage(newImage)
    tabText.setText(newValue.toString)
  }
}


