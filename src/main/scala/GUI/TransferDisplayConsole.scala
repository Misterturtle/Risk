package GUI

import javafx.beans.property.SimpleDoubleProperty
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.input.MouseEvent
import javafx.scene.layout.{CornerRadii, BackgroundFill, Background}

import Service._

import scalafx.beans.property.ReadOnlyDoubleProperty
import scalafx.geometry.Pos
import scalafx.scene.Group
import scalafx.scene.layout.{VBox, HBox}
import scalafx.scene.paint.Color
import scalafx.scene.shape.SVGPath
import scalafx.scene.text.Text

/**
  * Created by Harambe on 7/31/2017.
  */
class TransferDisplayConsole(wmUICont: WorldMapUIController, val sceneWidth: ReadOnlyDoubleProperty, val sceneHeight: ReadOnlyDoubleProperty, val windowXScale:SimpleDoubleProperty, val windowYScale: SimpleDoubleProperty) extends DisplayConsole {


  val increaseButton = new SVGPath()
  increaseButton.setContent("m22,0c-12.2,0-22,9.8-22,22s9.8,22 22,22 22-9.8 22-22-9.8-22-22-22zm9.7,19.7l-1.4,1.4c-0.4,0.4-1,0.4-1.4,0l-4-4c-0.3-0.3-0.9-0.1-0.9,0.4v16.5c0,0.6-0.4,1-1,1h-2c-0.6,0-1-0.4-1-1v-16.6c0-0.4-0.5-0.7-0.9-0.4l-4,4c-0.4,0.4-1,0.4-1.4,0l-1.4-1.4c-0.2-0.2-0.3-0.4-0.3-0.7s0.1-0.5 0.3-0.7l9-9c0.2-0.2 0.5-0.3 0.7-0.3 0.3,0 0.5,0.1 0.7,0.3l9,9c0.2,0.2 0.3,0.4 0.3,0.7 0,0.4-0.1,0.6-0.3,0.8z")
  increaseButton.setPickOnBounds(true)
  increaseButton.setOnMouseClicked(new EventHandler[MouseEvent] {
    override def handle(event: MouseEvent): Unit = {
      if(sourceCountryArmies.getText.toInt > 1){
        armiesToTransfer.setText((armiesToTransfer.getText.toInt + 1).toString)
        sourceCountryArmies.setText((sourceCountryArmies.getText.toInt -1).toString)
        targetCountryArmies.setText((targetCountryArmies.getText.toInt +1).toString)
      }

    }
  })

  val decreaseButton = new SVGPath()
  decreaseButton.setContent("M256,512c141.964,0,256-114.036,256-256S397.964,0,256,0S0,114.036,0,256S114.036,512,256,512z\n\t M143.127,282.764l16.291-16.291c4.654-4.654,11.637-4.654,16.291,0l46.545,46.545c3.492,3.492,10.473,1.164,10.473-4.654v-192\n\tc0-6.981,4.655-11.636,11.637-11.636h23.273c6.981,0,11.636,4.654,11.636,11.636v193.164c0,4.654,5.818,8.146,10.474,4.654\n\tl46.545-46.545c4.654-4.654,11.637-4.654,16.291,0l16.291,16.291c2.327,2.326,3.49,4.654,3.49,8.145c0,3.491-1.163,5.818-3.49,8.146\n\tL264.146,404.945c-2.327,2.328-5.818,3.491-8.146,3.491c-3.491,0-5.818-1.163-8.146-3.491L143.127,300.219\n\tc-2.327-2.328-3.49-4.655-3.49-8.146C139.637,287.418,140.8,285.091,143.127,282.764L143.127,282.764z")
  decreaseButton.setScaleX(.09)
  decreaseButton.setScaleY(.09)
  decreaseButton.setPickOnBounds(true)
  decreaseButton.setOnMouseClicked(new EventHandler[MouseEvent] {
    override def handle(event: MouseEvent): Unit = {
      if(armiesToTransfer.getText.toInt > 1){
        armiesToTransfer.setText((armiesToTransfer.getText.toInt - 1).toString)
        sourceCountryArmies.setText((sourceCountryArmies.getText.toInt +1).toString)
        targetCountryArmies.setText((targetCountryArmies.getText.toInt -1).toString)
      }

    }
  })




  val confirmButton = new SVGPath()
  confirmButton.setContent("M46.652,17.374c-13.817,0-24.999,11.182-25,25\n\tc0,13.818,11.182,25,25,25c13.819,0,25.001-11.182,25.001-25S60.472,17.374,46.652,17.374z M44.653,55.373c-6-3-11-6.999-16-9.999\n\tc1.347-3,2.722-4.499,5.722-5.499c2,4,6.278,3.499,8.278,6.499c5-6,10-12,16-17c2,0,3,3,6,3C63,36.625,48.653,45.374,44.653,55.373z")
  confirmButton.setFill(Color.valueOf("#91DC5A"))
  confirmButton.setPickOnBounds(true)
  confirmButton.setOnMouseClicked(new EventHandler[MouseEvent] {
    override def handle(event: MouseEvent): Unit = {
      closeAnim()
      wmUICont.receiveInput(ConfirmTransfer(armiesToTransfer.getText.toInt))
    }
  })


  val sourceCountryArmies = new Text()
  sourceCountryArmies.styleClass.add("defaultText")
  sourceCountryArmies.setScaleX(2)
  sourceCountryArmies.setScaleY(2)
  val sourceCountryName = new Text()
  sourceCountryName.styleClass.add("defaultText")
  sourceCountryName.setScaleX(2)
  sourceCountryName.setScaleY(2)


  val sourceCountryBox = new VBox()
  sourceCountryBox.alignment = Pos.Center
  sourceCountryBox.children.addAll(new Group(sourceCountryArmies), new Group(sourceCountryName))


  val targetCountryArmies = new Text()
  targetCountryArmies.styleClass.add("defaultText")
  targetCountryArmies.setScaleX(2)
  targetCountryArmies.setScaleY(2)
  val targetCountryName = new Text()
  targetCountryName.styleClass.add("defaultText")
  targetCountryName.setScaleX(2)
  targetCountryName.setScaleY(2)



  val targetCountryBox = new VBox()
  targetCountryBox.alignment = Pos.Center
  targetCountryBox.children.addAll(new Group(targetCountryArmies), new Group(targetCountryName))


  val armiesToTransfer = new Text()
  armiesToTransfer.setText("1")
  armiesToTransfer.styleClass.add("defaultText")
  armiesToTransfer.setScaleX(2)
  armiesToTransfer.setScaleY(2)

  val armySelectionBox = new HBox()
  armySelectionBox.children.addAll(new Group(decreaseButton), new Group(armiesToTransfer), new Group(increaseButton))
  armySelectionBox.spacing = 10
  armySelectionBox.alignment = Pos.Center



  val transferArmiesBox = new VBox()
  transferArmiesBox.spacing = 10
  transferArmiesBox.styleClass.add("defaultText")
  transferArmiesBox.children.addAll(armySelectionBox, new Group(confirmButton))
  transferArmiesBox.alignment = Pos.Center

  val completeLayoutBox = new HBox()
  completeLayoutBox.children.addAll(sourceCountryBox, transferArmiesBox, targetCountryBox)
  completeLayoutBox.styleClass.add("displayConsoleContent")


  val contentGroup = new Group(completeLayoutBox)

  scaleContent(windowXScale, windowYScale)
  alignment = Pos.Center
  styleClass.add("displayConsole")
  children.addAll(contentGroup)

  def startTransfer(sourceCountry:Country, targetCountry:Country): Unit ={
    sourceCountryArmies.setText((sourceCountry.armies -1).toString)
    sourceCountryName.setText(sourceCountry.name)
    armiesToTransfer.setText("1")
    targetCountryArmies.setText("1")
    targetCountryName.setText(targetCountry.name)
    val bg = new Background(new BackgroundFill(sourceCountry.owner.get.color, new CornerRadii(0,0,100,100, false), Insets.EMPTY))
    this.setBackground(bg)
    openAnim()
  }


}
