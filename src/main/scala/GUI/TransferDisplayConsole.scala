package GUI

import javafx.event.EventHandler
import javafx.scene.input.MouseEvent

import Service._

import scalafx.scene.paint.Color
import scalafx.scene.shape.SVGPath

/**
  * Created by Harambe on 7/31/2017.
  */
class TransferDisplayConsole(wmUICont: WorldMapUIController) extends DisplayConsole {


  val increaseButton = new SVGPath()



  val confirmButton = new SVGPath()
  confirmButton.setContent("M46.652,17.374c-13.817,0-24.999,11.182-25,25\n\tc0,13.818,11.182,25,25,25c13.819,0,25.001-11.182,25.001-25S60.472,17.374,46.652,17.374z M44.653,55.373c-6-3-11-6.999-16-9.999\n\tc1.347-3,2.722-4.499,5.722-5.499c2,4,6.278,3.499,8.278,6.499c5-6,10-12,16-17c2,0,3,3,6,3C63,36.625,48.653,45.374,44.653,55.373z")
  confirmButton.setFill(Color.valueOf("#91DC5A"))
  confirmButton.setPickOnBounds(true)
  confirmButton.setOnMouseClicked(new EventHandler[MouseEvent] {
    override def handle(event: MouseEvent): Unit = {
    }
  })

  val cancelButton = new SVGPath()
  cancelButton.setContent("M25.5,0.5c-13.816,0-24.999,11.182-25,25c0,13.818,11.181,25,25,25\n\tc13.82,0,25.002-11.182,25.002-25C50.501,11.682,39.32,0.5,25.5,0.5z M36.105,43.179l-10.609-10.61l-9.9,9.899l-7.07-7.069l9.9-9.9\n\tl-10.6-10.61l7.071-7.07l10.609,10.61l10.32-10.32l7.07,7.07l-10.33,10.32l10.609,10.61L36.105,43.179z")
  cancelButton.setFill(Color.valueOf("#FF0000"))
  cancelButton.setPickOnBounds(true)
  cancelButton.setOnMouseClicked(new EventHandler[MouseEvent] {
    override def handle(event: MouseEvent): Unit = {
      wmUICont.receiveInput(Retreat)
      closeAnim()
    }
  })



  def update(sourceCountry:Country, targetCountry:Country): Unit ={

  }


}
