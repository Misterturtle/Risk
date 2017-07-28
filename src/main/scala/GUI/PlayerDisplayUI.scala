package GUI

import javafx.beans.value.{ObservableValue, ChangeListener}
import javafx.geometry.Insets
import javafx.scene.layout.{Background, BackgroundFill, CornerRadii}
import javafx.scene.paint.{Paint, Color}

import Service.{WorldMapUIController, WorldMap}

import scalafx.geometry
import scalafx.geometry.{Pos}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{Priority, HBox, VBox}
import scalafx.scene.text.Text

/**
  * Created by Harambe on 7/20/2017.
  */
class PlayerDisplayUI(wmUICont:WorldMapUIController) extends VBox {

  val bg = new Background(new BackgroundFill(CustomColors.red, new CornerRadii(0,0,50,50, false), Insets.EMPTY))

  val nameContainer = new HBox()
  nameContainer.prefWidthProperty().bind(this.prefWidthProperty())
  nameContainer.prefHeightProperty().bind(this.prefHeightProperty().divide(2))
  //nameContainer.styleClass.add("overlayBackground")
  //nameContainer.styleClass.add("redBackground")
  nameContainer.setBackground(bg)

  val nameText = new Text(wmUICont.getPlayersName)
  nameText.styleClass.add("defaultText")

  nameContainer.alignment = Pos.Center
  nameContainer.children.add(nameText)



  val statsBar = new StatsBar(wmUICont.getPlayersColor, wmUICont.getPlayersArmies, wmUICont.getPlayersArmies)
  statsBar.enableScaling(this.prefHeightProperty(), this.prefWidthProperty())




  style = "-fx-padding: 10 0 0 0;"
  alignment = Pos.Center
  children.addAll(nameContainer, statsBar)


  def update(): Unit ={
    statsBar.update(wmUICont.getPlayersColor, wmUICont.getPlayersArmies, wmUICont.getPlayersTerritories)
    nameText.setText(wmUICont.getPlayersName)
    val radii = nameContainer.getBackground.getFills.get(0).getRadii
    val insets = nameContainer.getBackground.getFills.get(0).getInsets
    nameContainer.setBackground(new Background(new BackgroundFill(wmUICont.getPlayersColor, radii, insets)))
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
  armiesTab.enableScaling(this.heightProperty(), this.widthProperty())
  val territoriesTab = new StatTab(new Image(getClass().getResourceAsStream("/africa.png")), initTerritoryAmount)
  territoriesTab.enableScaling(this.heightProperty(), this.widthProperty())



  val leftSpacerBox = new HBox()

  margin = new geometry.Insets(new Insets(10,0,0,0))
  styleClass.add("grayBackground")
  styleClass.add("statsBar")
  children.addAll(leftSpacerBox, armiesTab, territoriesTab)



  def update(color:Paint, armyAmount: Int, territoryAmount: Int): Unit ={
    armiesTab.update(colorMap(color), armyAmount)
    territoriesTab.update(newValue = territoryAmount)
  }

  def enableScaling(heightProperty: javafx.beans.property.ReadOnlyDoubleProperty, widthProperty: javafx.beans.property.ReadOnlyDoubleProperty): Unit ={
    //Parent is playerDisplayUI
    this.prefHeightProperty().bind(heightProperty.divide(3))
  }
}



class StatTab(image:Image, value:Int) extends HBox {


  val tabImage = new ImageView(image)
  tabImage.preserveRatio = true
  tabImage.fitHeight = 1
  this.heightProperty().addListener(new ChangeListener[Number] {
    override def changed(observable: ObservableValue[_ <: Number], oldValue: Number, newValue: Number): Unit = {
      tabImage.fitHeight = newValue.doubleValue() - newValue.doubleValue()/5
      println("Hi")
    }
  })
  val imageContainer = new HBox()
  imageContainer.children.add(tabImage)
  imageContainer.alignment = Pos.Center
  imageContainer.style = "-fx-padding: 0 5 0 20"


  val tabText = new Text(value.toString)
  tabText.styleClass.add("defaultText")
  val textContainer = new HBox()
  textContainer.alignment = Pos.Center
  textContainer.children.add(tabText)
  textContainer.style = "-fx-padding: 0 20 0 5"


  styleClass.add("statTab")
  styleClass.add("grayBackground")
  alignment = Pos.Center


  children.addAll(imageContainer, textContainer)

  def update(newImage:Image = new Image(tabImage.getImage), newValue:Int): Unit = {
    tabImage.setImage(newImage)
    tabText.setText(newValue.toString)
  }

  def enableScaling(heightProperty: javafx.beans.property.ReadOnlyDoubleProperty, widthProperty: javafx.beans.property.ReadOnlyDoubleProperty) = {
    this.prefHeightProperty().bind(heightProperty)
    this.prefWidthProperty().bind(widthProperty.divide(5))
  }
}


