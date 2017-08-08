package GUI

import javafx.beans.property.SimpleDoubleProperty
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.input.MouseEvent
import javafx.scene.layout.{CornerRadii, BackgroundFill, Background}

import Service.{Battle, EndAttackPhase, Attacking, WorldMapUIController}

import scalafx.beans.property.ReadOnlyDoubleProperty
import scalafx.scene.Group
import scalafx.scene.text.Text

/**
  * Created by Harambe on 8/8/2017.
  */
class EndAttackPhaseButton(val sceneWidth: ReadOnlyDoubleProperty, val sceneHeight: ReadOnlyDoubleProperty, val windowXScale: SimpleDoubleProperty, val windowYScale: SimpleDoubleProperty) extends NextPhaseButton {

  val text = new Text("End Attack Phase")
  text.setScaleX(2)
  text.setScaleY(2)

  val contentGroup = new Group(text)


  def checkAnimationUpdate(wmUICont: WorldMapUIController): Unit ={

    wmUICont.getPhase match {

      case Attacking(_,_) =>
        if(!isDisplayed){
          println("Should set bg")
          this.setBackground(new Background(new BackgroundFill(wmUICont.getCurrPlayersColor, new CornerRadii(10), Insets.EMPTY)))
          setAction(() => endAttackPhase(wmUICont))
          openAnim()
        }

      case Battle(_,_,_,_) =>
        if(isDisplayed){
          setAction(() => {})
          closeAnim()
        }

      case _ =>

    }
  }

  private def setAction(action: () => Unit): Unit ={
    this.setOnMouseClicked(new EventHandler[MouseEvent] {
      override def handle(event: MouseEvent): Unit = action()
    })
  }

  private def endAttackPhase(wmUICont:WorldMapUIController): Unit = {
    wmUICont.receiveInput(EndAttackPhase)
    this.setOnMouseClicked(new EventHandler[MouseEvent] {
      override def handle(event: MouseEvent): Unit = {}
    })
  }


  this.style = "-fx-border-color: red"
  scaleContent(windowXScale, windowYScale)
  this.children.add(contentGroup)
}

