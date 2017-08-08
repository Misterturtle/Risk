package GUI

import javafx.beans.value.{ObservableValue, ChangeListener}
import javafx.event.{ActionEvent, EventHandler}
import javafx.geometry.Insets
import javafx.scene.layout.{Background, BackgroundFill, CornerRadii}
import javafx.scene.paint.{Paint, Color}

import Service.{WorldMapUIController, WorldMap}

import scalafx.animation.TranslateTransition
import scalafx.geometry
import scalafx.geometry.{Pos}
import scalafx.scene.Group
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{Priority, HBox, VBox}
import scalafx.scene.text.Text
import scalafx.util.Duration

/**
  * Created by Harambe on 7/20/2017.
  */
class PlayerDisplayUI(wmUICont:WorldMapUIController) extends VBox {

  val nameContainer = new HBox()
  nameContainer.prefWidthProperty().bind(this.prefWidthProperty())
  nameContainer.prefHeightProperty().bind(this.prefHeightProperty().divide(2))

  private val displayAnim = new TranslateTransition(new Duration(300), this)


  val nameText = new Text(wmUICont.getCurrPlayersName)
  nameText.styleClass.add("defaultText")

  nameContainer.alignment = Pos.Center
  nameContainer.children.add(nameText)

  val statsBar = new StatsBar(wmUICont.getCurrPlayersColor, wmUICont.getCurrPlayersArmies, wmUICont.getCurrPlayersArmies)
  statsBar.alignment = Pos.CenterLeft

  this.setTranslateX(-prefWidth.get)
  alignment = Pos.TopLeft
  children.addAll(nameContainer, new Group(statsBar))


  def update(): Unit ={
      statsBar.update(wmUICont.getCurrPlayersColor, wmUICont.getCurrPlayersArmies, wmUICont.getCurrPlayersTerritories)
      nameText.setText(wmUICont.getCurrPlayersName)
      nameContainer.setBackground(new Background(new BackgroundFill(wmUICont.getCurrPlayersColor, new CornerRadii(0,0,100,100, false), Insets.EMPTY)))
  }

  def openAnim(): Unit = {
    displayAnim.setOnFinished(new EventHandler[ActionEvent] {
      override def handle(event: ActionEvent): Unit = {}
    })
    displayAnim.setToX(0)
    displayAnim.playFromStart()
  }

  def closeAnim(): Unit ={
    displayAnim.setOnFinished(new EventHandler[ActionEvent] {
      override def handle(event: ActionEvent): Unit = {
        update()
        openAnim()
      }
    })
    displayAnim.setToX(-this.prefWidth.get)
    displayAnim.playFromStart()
  }
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
    Color.TRANSPARENT -> new Image(getClass.getResourceAsStream("/errorBox.png"))
  )


  val armiesTab = new StatTab(colorMap(initColor), initArmyAmount)
  armiesTab.prefHeight.bind(this.prefHeightProperty())
  armiesTab.prefWidth.bind(this.prefWidthProperty())

  val territoriesTab = new StatTab(new Image(getClass().getResourceAsStream("/africa.png")), initTerritoryAmount)
  territoriesTab.prefHeight.bind(this.prefHeightProperty())
  territoriesTab.prefWidth.bind(this.prefWidthProperty())

  children.addAll(armiesTab, territoriesTab)

  def update(color:Paint, armyAmount: Int, territoryAmount: Int): Unit ={
    armiesTab.update(colorMap(color), armyAmount)
    territoriesTab.update(newValue = territoryAmount)
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


