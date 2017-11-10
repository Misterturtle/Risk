package GUI

import java.lang.Boolean
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.event._
import javafx.scene.input.MouseEvent


import Service._

import scalafx.beans.property.ObjectProperty
import scalafx.scene.Group
import scalafx.scene.layout._
import scalafx.scene.shape.SVGPath
import scalafx.stage.Screen
import scalaz.Scalaz._

import Sugar.CustomSugar._

object CustomEH {
  var capturePhase = true
  private val highlightedCountry = ObjectProperty[Option[CountryUI]](None)
  highlightedCountry.delegate.addListener(new ChangeListener[Option[CountryUI]] {
    override def changed(observable: ObservableValue[_ <: Option[CountryUI]], oldValue: Option[CountryUI], newValue: Option[CountryUI]): Unit = {
      if(oldValue.nonEmpty)
        oldValue.get.noHighlight()

      if(newValue.nonEmpty)
        newValue.get.highlight()
      }
    }
  )

  def consumeEvent(event: MouseEvent): Unit = {
    event.consume()
    capturePhase = true
  }


}

class CustomEH(countriesUI: List[CountryUI]) extends EventHandler[MouseEvent] {

  import CustomEH._

  override def handle(event: MouseEvent): Unit = {
    if (!capturePhase) {
      event.getEventType match {

        case MouseEvent.MOUSE_CLICKED =>
          handleMouseClicked(event)

        case MouseEvent.MOUSE_MOVED =>
          handleMouseMoved(event)

        case _ =>
      }

      capturePhase = true
    }
    else {
      capturePhase = false
    }
  }

  def handleMouseClicked(event: MouseEvent): Unit = {
    countriesUI.map(_.countryDB.country).foreach(p => if (p.contains(p.sceneToLocal(event.getX, event.getY))) {
      p.getOnMouseClicked.handle(event)
    })
  }

  def handleMouseMoved(event: MouseEvent): Unit = {
    highlightedCountry.delegate.setValue(countriesUI.find(p => p.countryDB.country.contains(p.countryDB.country.sceneToLocal(event.getX, event.getY))))
  }
}

class WorldMapUI(messageService: (Input) => Unit) extends AnchorPane with PlayerListener with CountryListener with PhaseListener {

  private val self = this
  private val baseCoords = CountriesSVG.baseWindowCoord
  private val mapXScale = new SimpleDoubleProperty()
  mapXScale.bind(width.delegate.divide(baseCoords._1))
  private val mapYScale = new SimpleDoubleProperty()
  mapYScale.bind(height.delegate.divide(baseCoords._2))

  val windowXScale = new SimpleDoubleProperty()
  windowXScale.bind(width.delegate.divide(Screen.primary.bounds.getMaxX))
  val windowYScale = new SimpleDoubleProperty()
  windowYScale.bind(height.delegate.divide(Screen.primary.bounds.getMaxY - 20))

  var countriesUI = initCountries(CountryFactory.getCountries.map(_.name))

  this.stylesheets.add("worldStyle.css")
  this.styleClass.add("worldMap")

  val customEH: CustomEH = new CustomEH(countriesUI.values.toList)

  this.addEventHandler(MouseEvent.MOUSE_CLICKED, customEH)
  this.addEventFilter(MouseEvent.MOUSE_CLICKED, customEH)
  this.addEventHandler(MouseEvent.MOUSE_MOVED, customEH)
  this.addEventFilter(MouseEvent.MOUSE_MOVED, customEH)


  private def initCountries(countries: List[String]): Map[String, CountryUI] = {
    countries.map { c =>
      val ui = new CountryUI(c, CountriesSVG.countryLookup(c)(), () => messageService(CountryClicked(c)), mapXScale, mapYScale)
      this.width.addListener(ui.resizeXListener())
      this.height.addListener(ui.resizeYListener())
      this.children.addAll(ui.countryGroup, ui.animation, ui.ad)
      (c, ui)
    }.toMap
  }

  def disableWMInteractions(): Unit = {
    countriesUI.values.foreach{_.disableInteractions()}
  }

  def enableWMInteractions(): Unit = {
    countriesUI.values.foreach{_.enableInteractions()}
  }

  override def onPlayerChange(oldPlayer: Player, newPlayer: Player): () => Unit = () => {}

  override def onCountryChange(oldCountries: List[Country], newCountries: List[Country]): () => Unit = () => {
    val countriesToUpdate = newCountries diff oldCountries
    countriesToUpdate.foreach(c => countriesUI(c.name).update(c))
  }

  override def onPhaseChange(oldPhase: Phase, newPhase: Phase): () => Unit = {
    def exitBattle: Boolean = oldPhase.isInstanceOf[Battle] and !newPhase.isInstanceOf[Battle]
    def exitReinforcement: Boolean = oldPhase.isInstanceOf[Reinforcement] and !newPhase.isInstanceOf[Reinforcement]
    def attackToReinforcement: Boolean = oldPhase.isInstanceOf[Attacking] and newPhase.isInstanceOf[Reinforcement]
    def attackSourceSelected: Boolean = {
      val first = oldPhase match {
        case Attacking(None, _) => true
        case _ => false
      }
      val second = newPhase match {
        case Attacking(Some(_), _) => true
        case _ => false
      }
      first and second
    }
    def attackSourceDeselected: Boolean = {
      val first = oldPhase match {
        case Attacking(Some(_), _) => true
        case _ => false
      }
      val second = newPhase match {
        case Attacking(None, _) => true
        case _ => false
      }
      first and second
    }
    def attackTargetSelected: Boolean = {
      val first = oldPhase match {
        case Attacking(Some(_), _) => true
        case _ => false
      }
      val second = newPhase match {
        case Battle(_, _, _, _) => true
        case _ => false
      }
      first and second
    }
    def reinforcementSourceSelected: Boolean = {
      val first = oldPhase match {
        case Reinforcement(None, None) => true
        case _ => false
      }
      val second = newPhase match {
        case Reinforcement(Some(_), None) => true
        case _ => false
      }
      first and second
    }
    def reinforcementTargetSelected: Boolean = {
      val first = oldPhase match {
        case Reinforcement(Some(_), None) => true
        case _ => false
      }
      val second = newPhase match {
        case Reinforcement(Some(_), Some(_)) => true
        case _ => false
      }
      first and second
    }
    def reinforcementSourceDeselected: Boolean = {
      val first = oldPhase match {
        case Reinforcement(Some(_), None) => true
        case _ => false
      }
      val second = newPhase match {
        case Reinforcement(None, None) => true
        case _ => false
      }
      first and second
    }
    def reinforcementCanceled: Boolean = {
      val first = oldPhase match {
        case Reinforcement(Some(_), Some(_)) => true
        case _ => false
      }
      val second = newPhase match {
        case Reinforcement(None, None) => true
        case _ => false
      }
      first and second
    }
    def countryConquered: Boolean = {
      val first = oldPhase match {
        case Battle(_, _, _, false) => true
        case _ => false
      }
      val second = newPhase match {
        case Battle(_, _, _, true) => true
        case _ => false
      }
      first and second
    }

    Unit match {
      case _ if exitBattle => () => {
        countriesUI(oldPhase.asInstanceOf[Battle].source.name).deactivateAnimations()
        countriesUI(oldPhase.asInstanceOf[Battle].target.name).deactivateAnimations()
        countriesUI.values.foreach{_.enableInteractions()}
      }

      case _ if exitReinforcement => () => {
        oldPhase match {
          case Reinforcement(Some(s), None) =>
            countriesUI(s.name).deactivateAnimations()
            countriesUI.values.foreach{_.enableInteractions()}

          case Reinforcement(Some(s), Some(t)) =>
            countriesUI(s.name).deactivateAnimations()
            countriesUI(t.name).deactivateAnimations()
            countriesUI.values.foreach{_.enableInteractions()}

          case _ =>
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
        countriesUI.values.foreach{_.disableInteractions()}
      }

      case _ if attackToReinforcement => () => {
        oldPhase match {
          case Attacking(Some(s), _) =>
            countriesUI(s.name).deactivateAnimations()
            countriesUI.values.foreach{_.enableInteractions()}

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
        countriesUI.values.foreach{_.disableInteractions()}
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
        countriesUI.values.foreach{_.enableInteractions()}
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
