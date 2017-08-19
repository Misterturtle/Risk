package GUI

import javafx.beans.binding.DoubleBinding
import javafx.beans.property.{ReadOnlyDoubleProperty, SimpleDoubleProperty}
import javafx.beans.value.{ObservableValue, ChangeListener}
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.input.MouseEvent
import javafx.scene.layout.{CornerRadii, BackgroundFill, Background}

import Service._

import scalafx.animation.TranslateTransition
import scalafx.beans.property.ObjectProperty
import scalafx.geometry.Pos
import scalafx.scene.Group
import scalafx.scene.layout.{AnchorPane, HBox, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.shape.SVGPath

import Sugar.CustomSugar._

import scalafx.scene.text.Text
import scalafx.util.Duration

/**
  * Created by Harambe on 7/31/2017.
  */
class TransferConsole(messageService: (Input) => Unit, delayFromBattleCtrl: () => Duration, delayFromAttackCtrl: ()=> Duration, delayFromReinforceCtrl: ()=>Duration) extends VBox with Scaleable with PhaseListener with UIController {
  val self = this

  private var _sourceCountry:Option[Country] = None
  private var _targetCountry:Option[Country] = None

  private var _isDisplayed = false
  def isDisplayed = _isDisplayed
  private val openAnimation = new TranslateTransition(new Duration(270), this)
  private val closeAnimation = new TranslateTransition(new Duration(270), this)
  def getTimeToFinish: Duration = new Duration(closeAnimation.duration.value.subtract(closeAnimation.currentTime.value))


  val increaseButton = new SVGPath()
  increaseButton.setContent("m22,0c-12.2,0-22,9.8-22,22s9.8,22 22,22 22-9.8 22-22-9.8-22-22-22zm9.7,19.7l-1.4,1.4c-0.4,0.4-1,0.4-1.4,0l-4-4c-0.3-0.3-0.9-0.1-0.9,0.4v16.5c0,0.6-0.4,1-1,1h-2c-0.6,0-1-0.4-1-1v-16.6c0-0.4-0.5-0.7-0.9-0.4l-4,4c-0.4,0.4-1,0.4-1.4,0l-1.4-1.4c-0.2-0.2-0.3-0.4-0.3-0.7s0.1-0.5 0.3-0.7l9-9c0.2-0.2 0.5-0.3 0.7-0.3 0.3,0 0.5,0.1 0.7,0.3l9,9c0.2,0.2 0.3,0.4 0.3,0.7 0,0.4-0.1,0.6-0.3,0.8z")
  increaseButton.setPickOnBounds(true)
  def increaseButtonAction: () => Unit = {
    () => {
      if(sourceArmiesText.getText.toInt > 1){
        transferArmiesText.setText((transferArmiesText.getText.toInt + 1).toString)
        sourceArmiesText.setText((sourceArmiesText.getText.toInt -1).toString)
        targetArmiesText.setText((targetArmiesText.getText.toInt +1).toString)
      }
    }
  }

  val decreaseButton = new SVGPath()
  decreaseButton.setContent("M256,512c141.964,0,256-114.036,256-256S397.964,0,256,0S0,114.036,0,256S114.036,512,256,512z\n\t M143.127,282.764l16.291-16.291c4.654-4.654,11.637-4.654,16.291,0l46.545,46.545c3.492,3.492,10.473,1.164,10.473-4.654v-192\n\tc0-6.981,4.655-11.636,11.637-11.636h23.273c6.981,0,11.636,4.654,11.636,11.636v193.164c0,4.654,5.818,8.146,10.474,4.654\n\tl46.545-46.545c4.654-4.654,11.637-4.654,16.291,0l16.291,16.291c2.327,2.326,3.49,4.654,3.49,8.145c0,3.491-1.163,5.818-3.49,8.146\n\tL264.146,404.945c-2.327,2.328-5.818,3.491-8.146,3.491c-3.491,0-5.818-1.163-8.146-3.491L143.127,300.219\n\tc-2.327-2.328-3.49-4.655-3.49-8.146C139.637,287.418,140.8,285.091,143.127,282.764L143.127,282.764z")
  decreaseButton.setScaleX(.09)
  decreaseButton.setScaleY(.09)
  decreaseButton.setPickOnBounds(true)
  def decreaseButtonAction: () => Unit = {
    () => {
      if(transferArmiesText.getText.toInt > 1){
        transferArmiesText.setText((transferArmiesText.getText.toInt - 1).toString)
        sourceArmiesText.setText((sourceArmiesText.getText.toInt +1).toString)
        targetArmiesText.setText((targetArmiesText.getText.toInt -1).toString)
      }
    }
  }


  val confirmButton = new SVGPath()
  confirmButton.setContent("M46.652,17.374c-13.817,0-24.999,11.182-25,25\n\tc0,13.818,11.182,25,25,25c13.819,0,25.001-11.182,25.001-25S60.472,17.374,46.652,17.374z M44.653,55.373c-6-3-11-6.999-16-9.999\n\tc1.347-3,2.722-4.499,5.722-5.499c2,4,6.278,3.499,8.278,6.499c5-6,10-12,16-17c2,0,3,3,6,3C63,36.625,48.653,45.374,44.653,55.373z")
  confirmButton.setFill(Color.valueOf("#91DC5A"))
  confirmButton.setPickOnBounds(true)
  def confirmButtonAction: ()=> Unit = () => {
    disableButtons()
    messageService(ConfirmTransfer(transferArmiesText.getText.toInt))
  }

  val cancelButton = new SVGPath()
  cancelButton.setContent("M25.5,0.5c-13.816,0-24.999,11.182-25,25c0,13.818,11.181,25,25,25\n\tc13.82,0,25.002-11.182,25.002-25C50.501,11.682,39.32,0.5,25.5,0.5z M36.105,43.179l-10.609-10.61l-9.9,9.899l-7.07-7.069l9.9-9.9\n\tl-10.6-10.61l7.071-7.07l10.609,10.61l10.32-10.32l7.07,7.07l-10.33,10.32l10.609,10.61L36.105,43.179z")
  cancelButton.setFill(Color.valueOf("#FF0000"))
  cancelButton.setPickOnBounds(true)
  def cancelButtonAction: () => Unit = () => {
    disableButtons()
    messageService(CancelTransfer)
  }

  val sourceArmiesText = new Text()
  sourceArmiesText.styleClass.add("defaultText")
  sourceArmiesText.setScaleX(2)
  sourceArmiesText.setScaleY(2)
  val sourceCountryName = new Text()
  sourceCountryName.styleClass.add("defaultText")
  sourceCountryName.setScaleX(2)
  sourceCountryName.setScaleY(2)


  val sourceCountryBox = new VBox()
  sourceCountryBox.alignment = Pos.Center
  sourceCountryBox.children.addAll(new Group(sourceArmiesText), new Group(sourceCountryName))


  val targetArmiesText = new Text()
  targetArmiesText.styleClass.add("defaultText")
  targetArmiesText.setScaleX(2)
  targetArmiesText.setScaleY(2)
  val targetCountryName = new Text()
  targetCountryName.styleClass.add("defaultText")
  targetCountryName.setScaleX(2)
  targetCountryName.setScaleY(2)



  val targetCountryBox = new VBox()
  targetCountryBox.alignment = Pos.Center
  targetCountryBox.children.addAll(new Group(targetArmiesText), new Group(targetCountryName))


  val transferArmiesText = new Text()
  transferArmiesText.setText("1")
  transferArmiesText.styleClass.add("defaultText")
  transferArmiesText.setScaleX(2)
  transferArmiesText.setScaleY(2)

  val armySelectionBox = new HBox()
  armySelectionBox.children.addAll(new Group(decreaseButton), new Group(transferArmiesText), new Group(increaseButton))
  armySelectionBox.spacing = 10
  armySelectionBox.alignment = Pos.Center

  val decisionBox = new HBox()
  decisionBox.children.add(confirmButton)
  decisionBox.spacing = 20
  decisionBox.alignment = Pos.Center



  val transferArmiesBox = new VBox()
  transferArmiesBox.spacing = 10
  transferArmiesBox.styleClass.add("defaultText")
  transferArmiesBox.children.addAll(armySelectionBox, new Group(decisionBox))
  transferArmiesBox.alignment = Pos.Center

  val completeLayoutBox = new HBox()
  completeLayoutBox.children.addAll(sourceCountryBox, transferArmiesBox, targetCountryBox)
  completeLayoutBox.styleClass.add("displayConsoleContent")


  val contentGroup = new Group(completeLayoutBox)

  alignment = Pos.Center
  styleClass.add("displayConsole")
  children.addAll(contentGroup)

  private def enableButtons(): Unit ={
    increaseButton.setOnMouseClicked(increaseButtonAction)
    decreaseButton.setOnMouseClicked(decreaseButtonAction)
    confirmButton.setOnMouseClicked(confirmButtonAction)
    cancelButton.setOnMouseClicked(cancelButtonAction)
  }

  private def disableButtons(): Unit ={
    increaseButton.setOnMouseClicked(()=>{})
    decreaseButton.setOnMouseClicked(()=>{})
    confirmButton.setOnMouseClicked(()=>{})
    cancelButton.setOnMouseClicked(()=>{})
  }


  private def resetTransfer(): Unit ={
    transferArmiesText.setText("1")
  }

  private def updateSource(): Unit ={
    sourceArmiesText.setText((_sourceCountry.get.armies -1).toString)
    sourceCountryName.setText(_sourceCountry.get.name)
  }

  private def updateTarget(): Unit = {
    targetArmiesText.setText("1")
    targetCountryName.setText(_targetCountry.get.name)
  }

  private def updateColor(): Unit ={
    val bg = new Background(new BackgroundFill(_sourceCountry.get.owner.get.color, new CornerRadii(0,0,100,100, false), Insets.EMPTY))
    this.setBackground(bg)
  }

  private def openAnim(onFinish: () => Unit): Unit = {
    openAnimation.setOnFinished(onFinish)
    _isDisplayed = true
    openAnimation.setToY(0)
    openAnimation.playFromStart()
  }

  private def closeAnim(onFinish: () => Unit): Unit = {
    closeAnimation.setOnFinished(onFinish)
    _isDisplayed = false
    closeAnimation.setToY(-this.height.get)
    closeAnimation.playFromStart()
  }

  override def anchorResize(sceneWidth: ReadOnlyDoubleProperty, sceneHeight: ReadOnlyDoubleProperty): Unit = {
    sceneWidth.addListener(new ChangeListener[Number] {
      override def changed(observable: ObservableValue[_ <: Number], oldValue: Number, newValue: Number): Unit = {
        AnchorPane.setLeftAnchor(self, newValue.doubleValue() * .3)
        AnchorPane.setRightAnchor(self, newValue.doubleValue() * .3)
      }
    })

    sceneHeight.addListener(new ChangeListener[Number] {
      override def changed(observable: ObservableValue[_ <: Number], oldValue: Number, newValue: Number): Unit = {
        AnchorPane.setTopAnchor(self, 0)
        if (!_isDisplayed) {
          self.translateY.setValue(-self.height.value)
        }
      }
    })
  }

  override def bindScale(windowScaleX: DoubleBinding, windowScaleY: DoubleBinding): Unit = {
    contentGroup.scaleX.bind(windowScaleX)
    contentGroup.scaleY.bind(windowScaleY)
  }

  override def bindPrefSize(sceneWidth: ReadOnlyDoubleProperty, sceneHeight: ReadOnlyDoubleProperty): Unit = {
    prefHeight.bind(sceneHeight.divide(4))
    prefWidth.bind(sceneWidth.divide(10))
  }

  override def init(sceneWidth: ReadOnlyDoubleProperty, sceneHeight: ReadOnlyDoubleProperty): Unit = {
    AnchorPane.setLeftAnchor(self, sceneWidth.doubleValue() * .3)
    AnchorPane.setRightAnchor(self, sceneWidth.doubleValue() * .3)
    AnchorPane.setTopAnchor(self, 0)
    self.translateY.setValue(-self.height.value)
  }

  override def onPhaseChange(oldPhase: Phase, newPhase: Phase): () => Unit = {

    def fromBattle: Boolean = {
      val first = oldPhase match {case Battle(_,_,_,false) => true case _=> false}
      val second = newPhase match {case Battle(_,_,_,true) => true case _=> false}
      first and second
    }

    def fromReinforcement: Boolean = {
      val first = oldPhase match {case Reinforcement(Some(_),None) => true case _=> false}
      val second = newPhase match {case Reinforcement(Some(_),Some(_)) => true case _=> false}
      first and second
    }

    def toReinforcement: Boolean = {
      val first = oldPhase match {case Reinforcement(Some(_), Some(_)) => true case _=> false}
      val second = newPhase match {case Reinforcement(None, None) => true case _=> false}
      first and second
    }

    def toAttack: Boolean = {
      val first = oldPhase match {case Battle(_,_,_,true) => true case _=> false}
      val second = newPhase match {case Attacking(_,_) => true case _=> false}
      first and second
    }

    def toEndTurn: Boolean = {
      val first = oldPhase match {case Reinforcement(_,_)=> true case _=> false}
      val second = newPhase match {case x if !x.isInstanceOf[Reinforcement] => true case _=> false}
      first and second
    }

    Unit match {

      case _ if fromBattle => () => {
        _sourceCountry = Some(_phase.value.asInstanceOf[Battle].source)
        _targetCountry = Some(_phase.value.asInstanceOf[Battle].target)
        disableButtons()
        updateSource()
        updateTarget()
        updateColor()
        resetTransfer()
        openAnimation.setDelay(delayFromBattleCtrl())
        openAnim(()=>enableButtons())
      }

      case _ if fromReinforcement => () => {
        _sourceCountry = Some(_phase.value.asInstanceOf[Reinforcement].source.get)
        _targetCountry = Some(_phase.value.asInstanceOf[Reinforcement].target.get)
        disableButtons()
        updateSource()
        updateTarget()
        updateColor()
        decisionBox.children.add(cancelButton)
        resetTransfer()
        openAnimation.setDelay(delayFromReinforceCtrl())
        openAnim(()=>enableButtons())
      }

      case _ if toReinforcement => () => {
        _sourceCountry = None
        _targetCountry = None
        disableButtons()
        closeAnim(()=>{decisionBox.children.remove(cancelButton)})
      }

      case _ if toAttack => () => {
        _sourceCountry = None
        _targetCountry = None
        disableButtons()
        closeAnim(()=>{})
      }

      case _ if toEndTurn => () => {
        disableButtons()
        closeAnim(()=>{decisionBox.children.remove(cancelButton)})
      }

      case _=> ()=> {}
    }
  }
}
