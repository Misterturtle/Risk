package GUI

import java.lang.Boolean
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.event._
import javafx.scene.input.MouseEvent

import Service._

import scala.collection.mutable.ListBuffer
import scalafx.scene.Group
import scalafx.scene.image.Image
import scalafx.scene.layout._
import scalafx.stage.Screen
import scalaz.Scalaz._

import Sugar.CustomSugar._

object CustomEH{
  var capturePhase = true
  def consumeEvent(event:MouseEvent): Unit ={
    event.consume()
    capturePhase = true
  }
}

class WorldMapUI(wmUICont: WorldMapUIController, messageService: (Input) => Unit) extends AnchorPane with PlayerListener with CountryListener with PhaseListener {

  private val self = this
  private val mapImage = new Image("map.png")
  private val mapXScale = new SimpleDoubleProperty()
  mapXScale.bind(width.delegate.divide(mapImage.width.value))
  private val mapYScale = new SimpleDoubleProperty()
  mapYScale.bind(height.delegate.divide(mapImage.height.value))

  val windowXScale = new SimpleDoubleProperty()
  windowXScale.bind(width.delegate.divide(Screen.primary.bounds.getMaxX))
  val windowYScale = new SimpleDoubleProperty()
  windowYScale.bind(height.delegate.divide(Screen.primary.bounds.getMaxY - 20))

  var countriesUI = initCountries(CountryFactory.getCountries.map(_.name))
  countriesUI.foreach { case (name, coun) =>
    this.width.addListener(coun.resizeXListener())
    this.height.addListener(coun.resizeYListener())
  }

  val playerDisplay = new PlayerDisplayUI(wmUICont)
  playerDisplay.prefHeightProperty().bind(this.heightProperty().divide(10))
  playerDisplay.prefWidthProperty().bind(this.widthProperty().divide(4))
  playerDisplay.statsBar.scaleX.bind(windowXScale)
  playerDisplay.statsBar.scaleY.bind(windowYScale)

  this.stylesheets.add("worldStyle.css")
  this.styleClass.add("worldMap")

  val customEH:CustomEH = new CustomEH()

  class CustomEH() extends EventHandler[MouseEvent] {
    import CustomEH._

    override def handle(event: MouseEvent): Unit = {
      if(!capturePhase){
        println("Mouse Clicked Event")
        countriesUI.values.map(_.polygon).foreach(p => if(p.contains(p.sceneToLocal(event.getX, event.getY))){
          p.getOnMouseClicked.handle(event)
        })
        capturePhase = true
      }
      else {
        capturePhase = false
      }
    }
  }

  this.addEventHandler(MouseEvent.MOUSE_CLICKED, customEH)
  this.addEventFilter(MouseEvent.MOUSE_CLICKED, customEH)
  children.addAll(playerDisplay)


  private def initCountries(countries: List[String]): Map[String, CountryUI] = {
    countries.map { c =>
      val ui = new CountryUI(c, PixelDatabase.lookup(c), () => messageService(CountryClicked(c)), mapXScale, mapYScale)
      this.children.add(ui)
      ui.initPoly()
      (c, ui)
    }.toMap
  }

  override def onPlayerChange(oldPlayer: Player, newPlayer: Player): () => Unit = () => {}

  override def onCountryChange(oldCountries: List[Country], newCountries: List[Country]): () => Unit = () => {
    val countriesToUpdate = newCountries diff oldCountries
    countriesToUpdate.foreach(c => countriesUI(c.name).update(c))
  }

  override def onPhaseChange(oldPhase: Phase, newPhase: Phase): () => Unit = {
    def exitBattle:Boolean = oldPhase.isInstanceOf[Battle] and !newPhase.isInstanceOf[Battle]
    def exitReinforcement:Boolean = oldPhase.isInstanceOf[Reinforcement] and !newPhase.isInstanceOf[Reinforcement]
    def attackToReinforcement:Boolean = oldPhase.isInstanceOf[Attacking] and newPhase.isInstanceOf[Reinforcement]
    def attackSourceSelected:Boolean = {
      val first = oldPhase match{case Attacking(None, _) => true case _=> false}
      val second = newPhase match{case Attacking(Some(_), _) => true case _=> false}
      first and second
    }
    def attackSourceDeselected:Boolean = {
      val first = oldPhase match{case Attacking(Some(_), _) => true case _=> false}
      val second = newPhase match{case Attacking(None, _) => true case _=> false}
      first and second
    }
    def attackTargetSelected:Boolean = {
      val first = oldPhase match{case Attacking(Some(_), _) => true case _=> false}
      val second = newPhase match{case Battle(_,_,_,_) => true case _=> false}
      first and second
    }
    def reinforcementSourceSelected:Boolean = {
      val first = oldPhase match{case Reinforcement(None, None) => true case _=> false}
      val second = newPhase match{case Reinforcement(Some(_), None) => true case _=> false}
      first and second
    }
    def reinforcementTargetSelected:Boolean = {
      val first = oldPhase match{case Reinforcement(Some(_), None) => true case _=> false}
      val second = newPhase match{case Reinforcement(Some(_), Some(_)) => true case _=> false}
      first and second
    }
    def reinforcementSourceDeselected: Boolean = {
      val first = oldPhase match {case Reinforcement(Some(_), None) => true case _=> false}
      val second = newPhase match {case Reinforcement(None, None) => true case _=> false}
      first and second
    }
    def reinforcementCanceled: Boolean = {
      val first = oldPhase match {case Reinforcement(Some(_), Some(_)) => true case _=> false}
      val second = newPhase match {case Reinforcement(None, None) => true case _=> false}
      first and second
    }
    def countryConquered:Boolean = {
      val first = oldPhase match {case Battle(_,_,_,false) => true case _=>false}
      val second = newPhase match {case Battle(_,_,_,true) => true case _=> false}
      first and second
    }

    Unit match {
      case _ if exitBattle => () => {
        countriesUI(oldPhase.asInstanceOf[Battle].source.name).deactivateAnimations()
        countriesUI(oldPhase.asInstanceOf[Battle].target.name).deactivateAnimations()
      }

      case _ if exitReinforcement => () => {
        oldPhase match {
          case Reinforcement(Some(s), None) =>
            countriesUI(s.name).deactivateAnimations()

          case Reinforcement(Some(s), Some(t)) =>
            countriesUI(s.name).deactivateAnimations()
            countriesUI(t.name).deactivateAnimations()

          case _=>
        }
      }


      case _ if attackSourceSelected => () => {
        val source = newPhase.asInstanceOf[Attacking].source.get
        countriesUI(source.name).activateSourceCountryAnim(source.owner.get.color)
      }

      case _ if attackSourceDeselected => () => {
        val source = oldPhase.asInstanceOf[Attacking].source.get
        countriesUI(source.name).deactivateAnimations()
      }

      case _ if attackTargetSelected => () => {
        val target = newPhase.asInstanceOf[Battle].target
        countriesUI(target.name).activateTargetCountryAnim(target.owner.get.color)
      }

      case _ if attackToReinforcement => () => {
        oldPhase match {
          case Attacking(Some(s), _) =>
            countriesUI(s.name).deactivateAnimations()

          case _ =>
        }
      }

      case _ if reinforcementSourceSelected => () => {
        val source = newPhase.asInstanceOf[Reinforcement].source.get
        countriesUI(source.name).activateSourceCountryAnim(source.owner.get.color)
      }

      case _ if reinforcementTargetSelected => () => {
        val target = newPhase.asInstanceOf[Reinforcement].target.get
        countriesUI(target.name).activateTargetCountryAnim(target.owner.get.color)
      }

      case _ if reinforcementSourceDeselected => () => {
        val source = oldPhase.asInstanceOf[Reinforcement].source.get
        countriesUI(source.name).deactivateAnimations()
      }

      case _ if reinforcementCanceled => () => {
        val source = oldPhase.asInstanceOf[Reinforcement].source.get
        val target = oldPhase.asInstanceOf[Reinforcement].target.get
        countriesUI(source.name).deactivateAnimations()
        countriesUI(target.name).deactivateAnimations()
      }

      case _ if countryConquered => () => {
        val country = newPhase.asInstanceOf[Battle].target
        countriesUI(country.name).activateTargetCountryAnim(country.owner.get.color)
      }

      case _ => () => {
      }
    }
  }
}
